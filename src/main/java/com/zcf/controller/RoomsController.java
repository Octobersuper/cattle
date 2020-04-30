package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.game_center.PK_WebSocket;
import com.zcf.game_util.CreatRoom;
import com.zcf.game_util.GameService;
import com.zcf.game_util.Time_Room;
import com.zcf.mapper.RobotMapper;
import com.zcf.mapper.RoomsMapper;
import com.zcf.pojo.Robot;
import com.zcf.pojo.Rooms;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-09
 */
@Component
@CrossOrigin
@RestController
@RequestMapping("/rooms")
public class RoomsController {

    @Autowired
    RoomsMapper rm;
    @Autowired
    RobotMapper robotMapper;

    /**
     * @ Author:ZhaoQi
     * @ methodName:获取房间列表
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @GetMapping("get")
    public LayuiJson get(Rooms rooms, @RequestParam(value = "pageNum") int pageNum,
                         @RequestParam(value = "pageSize") int pageSize) {
        LayuiJson lj = new LayuiJson();
        EntityWrapper<Rooms> ew = new EntityWrapper<>();
        if (rooms.getRoomnumber() != null && rooms.getRoomnumber() != 0) {
            ew.eq("roomnumber", rooms.getRoomnumber());
        }
        ew.orderBy("createtime", false);
        List<Rooms> list = rm.selectPage(new Page<Rooms>(pageNum, pageSize), ew);
        Integer count = rm.selectCount(null);
        if (list != null) {
            for (Rooms room :
                    list) {
                RoomBean roomBean = Public_State.PKMap.get(String.valueOf(room.getRoomnumber()));
                int size = roomBean.getGame_userList().size();
                if (size == 0) {
                    room.setRoom_state(0);
                } else {
                    room.setRoom_state(1);
                }
                room.setUser_number(size);
                room.setGame_number(roomBean.getGame_number());
            }
            lj.setCode(0);
            lj.setCount(count);
            lj.setData(list);
            lj.setMsg("成功");
            return lj;
        }
        lj.setCode(1);
        lj.setCount(count);
        lj.setData(null);
        lj.setMsg("暂无数据");
        return lj;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:修改房间信息
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @PostMapping("update")
    public Body update(Rooms rooms) {
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean != null && rooms.getRobot() != null) {
            int ai = 0;
            for (UserBean user :
                    roomBean.getGame_userList(0)) {
                if (user.getIsAi() == 1) {
                    ai++;
                }
            }
            if (ai != rooms.getRobot()) {
                int users = rooms.getRobot() - ai;
                if (users > 0) {
                    GameService gs = new GameService();
                    //添加机器人
                    for (int i = 0; i < users; i++) {
                        UserBean user = new UserBean();
                        user.Initialization();
                        int j = 0;
                        do {
                            j++;
                            if (j == 6) {
                                break;
                            }
                            //去机器人库随机找出一个机器人 最多找五次
                            Robot robot = robotMapper.getRodom();
                            if (robot == null) {
                                break;
                            }else{
                                if(Public_State.ISUser_Room(Integer.valueOf(String.valueOf(robot.getId())))){
                                    break;
                                }
                            }
                            user.setUserid(Integer.valueOf(String.valueOf(robot.getId())));
                            user.setNickname(robot.getName());
                            user.setAvatarurl(robot.getImg());
                            user.setWinodds(robot.getWinodds());
                            user.setIsAi(1);
                            if (roomBean.getRoom_state() == 0) {
                                user.setGametype(1);
                                user.setUsertype(1);
                            } else {
                                user.setGametype(3);
                                user.setUsertype(0);
                            }
                            int max = 10000;
                            int min = 3000;
                            Random random = new Random();
                            double s = (double) random.nextInt(max) % (max - min + 1) + min;
                            user.setMoney(s);
                            user.setfId((long) 0);
                        } while (roomBean.getUserBean(user.getUserid()) != null);
                        if (user.getUserid() != 0) {
                            roomBean.getGame_userList().add(user);
                            if (user.getGametype() == 1) {
                                gs.Sit_down(user,roomBean);
                                //发送坐下的消息
                                HashMap<String, Object> returnMap = new HashMap<>();
                                returnMap.put("state", "0"); // 坐下成功
                                returnMap.put("userid", user.getUserid());
                                returnMap.put("type", "Sit_down");
                                returnMap.put("user_positions", roomBean.getUser_positions());
                                roomBean.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,"");
                                PK_WebSocket socket = Public_State.getPkWebSocket(roomBean.getRoom_number());
                                if (socket != null) {
                                    socket.sendMessageToAll(returnMap, roomBean);
                                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                                }
                                //判断房间人数是否需要开启游戏线程
                                if (roomBean.getGame_userList(0).size() == 2) {
                                    new Time_Room(roomBean, gs).start();
                                }
                            }
                        }
                    }
                } else {
                    //减少机器人
                    ArrayList<UserBean> list = new ArrayList<>();
                    for (int i = 0; i < ai - rooms.getRobot(); i++) {
                        for (UserBean user :
                                roomBean.getGame_userList(0)) {
                            if(roomBean.getRoom_state()!=0){
                                if (user.getIsAi() == 1) {
                                    user.setGametype(2);
                                    break;
                                }
                            }else{
                                if (user.getIsAi() == 1) {
                                    roomBean.remove_options(user.getUserid());
                                    list.add(user);
                                }
                            }
                        }
                    }
                    roomBean.getGame_userList().removeAll(list);
                }
            }
            PK_WebSocket pkWebSocket = Public_State.getPkWebSocket2(String.valueOf(rooms.getRoomnumber()));
            if (pkWebSocket != null) {
                pkWebSocket.Room_change(roomBean,0);
            }
        }else if (roomBean != null && roomBean.getGame_userList().size() != 0) {
            return Body.newInstance(451, "房间处于游戏状态，不可更改");
        }
        boolean b = rooms.updateById();
        if (b) {
            Rooms select = rm.selectById(rooms.getId());
            roomBean.setDi_fen(select.getFen());
            roomBean.setJion_fen(select.getJionfen());
            roomBean.setMax_number(select.getMaxnumber());
            roomBean.setRoom_type(select.getRoomtype());
            roomBean.setWater((double) select.getWater() / 100);
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:删除房间信息
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @PostMapping("delete")
    public Body delete(Rooms rooms) {
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean != null && roomBean.getGame_userList().size() != 0) {
            return Body.newInstance(451, "房间处于游戏状态，不可删除");
        }
        boolean b = rooms.deleteById();
        if (b) {
            Public_State.PKMap.remove(String.valueOf(rooms.getRoomnumber()));
            System.err.println("当前房间数：" + Public_State.PKMap.size() + "-------------");
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:添加房间信息
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @PostMapping("insert")
    public Body insert(Rooms rooms) {
        Double i;
        long round;
        while (true) {
            i = (Math.random() * 9 + 1) * 100000;
            round = Math.round(i);
            RoomBean roomBean = CreatRoom.EcoSocket(String.valueOf(round));
            if (roomBean != null) {
                roomBean.setMax_number(rooms.getMaxnumber());
                roomBean.setJion_fen(rooms.getJionfen());
                roomBean.setDi_fen(rooms.getFen());
                roomBean.setRoom_type(rooms.getRoomtype());
                roomBean.setWater((double) rooms.getWater() / 100);
                PK_WebSocket pkWebSocket = Public_State.getPkWebSocket2(String.valueOf(rooms.getRoomnumber()));
                if (pkWebSocket != null) {
                    pkWebSocket.Room_change(roomBean,4);
                }
                break;
            }
        }
        rooms.setRoomnumber(Integer.valueOf(String.valueOf(round)));
        rooms.setCreatetime(new Date());
        boolean b = rooms.insert();
        if (b) {
            System.err.println("当前房间数：" + Public_State.PKMap.size() + "-------------");
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:房间初始化
     * @ Description:
     * @ Return:
     * @ Date:2020/4/11
     */
    @PostConstruct
    public void roomInit() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Rooms> rooms = rm.selectList(null);
            GameService gs = new GameService();
            if (rooms.size() != 0) {
                for (Rooms room :
                        rooms) {
                    RoomBean roomBean = CreatRoom.EcoSocket(String.valueOf(room.getRoomnumber()));
                    if (roomBean != null) {
                        roomBean.setMax_number(room.getMaxnumber());
                        roomBean.setJion_fen(room.getJionfen());
                        roomBean.setDi_fen(room.getFen());
                        roomBean.setRoom_type(room.getRoomtype());
                        roomBean.setWater((double) room.getWater() / 100);
                        if (room.getRobot() != 0) {
                            for (int i = 0; i < room.getRobot(); i++) {
                                UserBean user = new UserBean();
                                user.Initialization();
                                int j = 0;
                                do {
                                    j++;
                                    if (j == 6) {
                                        user = null;
                                        break;
                                    }
                                    //去机器人库随机找出一个机器人 最多找五次
                                    Robot robot = robotMapper.getRodom();
                                    if (robot != null) {
                                        user.setUserid(Integer.valueOf(String.valueOf(robot.getId())));
                                        user.setNickname(robot.getName());
                                        user.setAvatarurl(robot.getImg());
                                        user.setWinodds(robot.getWinodds());
                                        user.setUsertype(1);
                                        user.setIsAi(1);
                                        int max = 10000;
                                        int min = 3000;
                                        Random random = new Random();
                                        double s = (double) random.nextInt(max) % (max - min + 1) + min;
                                        user.setMoney(s);
                                        user.setfId((long) 0);
                                    } else {
                                        break;
                                    }
                                } while (roomBean.getUserBean(user.getUserid()) != null);
                                if (user!=null && user.getUserid() != 0) {
                                    gs.Sit_down(user, roomBean);
                                    roomBean.getGame_userList().add(user);
                                }
                            }
                        }
                        if (roomBean.getGame_userList(0).size() >= 2) {
                            new Time_Room(roomBean, gs).start();
                        }
                    }
                }
            }
        }).start();
        System.err.println("当前房间数：" + Public_State.PKMap.size() + "-------------");
    }
}

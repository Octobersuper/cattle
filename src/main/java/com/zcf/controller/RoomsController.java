package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_util.CreatRoom;
import com.zcf.mapper.RoomsMapper;
import com.zcf.pojo.Rooms;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
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

    /**
     *@ Author:ZhaoQi
     *@ methodName:获取房间列表
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @GetMapping("get")
    public LayuiJson get(Rooms rooms, @RequestParam(value = "pageNum") int pageNum,
                         @RequestParam(value = "pageSize") int pageSize){
        LayuiJson lj = new LayuiJson();
        EntityWrapper<Rooms> ew = new EntityWrapper<>();
        if(rooms.getRoomnumber()!=null && rooms.getRoomnumber()!=0){
            ew.eq("roomnumber",rooms.getRoomnumber());
        }
        ew.orderBy("createtime",false);
        List<Rooms> list = rm.selectPage(new Page<Rooms>(pageNum, pageSize), ew);
        Integer count = rm.selectCount(null);
        if (list != null) {
            for (Rooms room :
                    list) {
                RoomBean roomBean = Public_State.PKMap.get(String.valueOf(room.getRoomnumber()));
                int size = roomBean.getGame_userList().size();
                if(size==0){
                    room.setRoom_state(0);
                }else{
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
     *@ Author:ZhaoQi
     *@ methodName:修改房间信息
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("update")
    public Body update(Rooms rooms){
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean.getGame_userList().size()!=0){
            return Body.newInstance(451,"房间处于游戏状态，不可更改");
        }
        boolean b = rooms.updateById();
        if(b){
            Rooms select = rm.selectById(rooms.getId());
            roomBean.setDi_fen(select.getFen());
            roomBean.setJion_fen(select.getJionfen());
            roomBean.setMax_number(select.getMaxnumber());
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:删除房间信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("delete")
    public Body delete(Rooms rooms){
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean.getGame_userList().size()!=0){
            return Body.newInstance(451,"房间处于游戏状态，不可删除");
        }
        boolean b = rooms.deleteById();
        if(b){
            Public_State.PKMap.remove(String.valueOf(rooms.getRoomnumber()));
            System.err.println("当前房间数："+Public_State.PKMap.size()+"-------------");
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:添加房间信息
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("insert")
    public Body insert(Rooms rooms){
        Double i;
        long round;
        while (true){
            i = (Math.random()*9+1)*100000;
            round = Math.round(i);
            RoomBean roomBean = CreatRoom.EcoSocket(String.valueOf(round));
            if(roomBean!=null){
                roomBean.setMax_number(rooms.getMaxnumber());
                roomBean.setJion_fen(rooms.getJionfen());
                roomBean.setDi_fen(rooms.getFen());
                roomBean.setRoom_type(rooms.getRoomtype());
                roomBean.setWater((double)rooms.getWater()/100);
                break;
            }
        }
        rooms.setRoomnumber(Integer.valueOf(String.valueOf(round)));
        rooms.setCreatetime(new Date());
        boolean b = rooms.insert();
        if(b){
            System.err.println("当前房间数："+Public_State.PKMap.size()+"-------------");
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:房间初始化
     *@ Description:
     *@ Return:
     *@ Date:2020/4/11
     */
    @PostConstruct
    public void roomInit(){
        new Thread(()-> {
            List<Rooms> rooms = rm.selectList(null);
            if (rooms.size()!=0) {
                for (Rooms room :
                        rooms) {
                    RoomBean roomBean = CreatRoom.EcoSocket(String.valueOf(room.getRoomnumber()));
                    if(roomBean!=null){
                        roomBean.setMax_number(room.getMaxnumber());
                        roomBean.setJion_fen(room.getJionfen());
                        roomBean.setDi_fen(room.getFen());
                        roomBean.setRoom_type(room.getRoomtype());
                        roomBean.setWater((double)room.getWater()/100);
                    }
                }
            }
            try {
                Thread.sleep(300000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        System.err.println("当前房间数："+Public_State.PKMap.size()+"-------------");
    }
}


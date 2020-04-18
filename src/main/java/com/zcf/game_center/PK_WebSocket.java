/**
 *
 */
package com.zcf.game_center;


import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.game_util.GameService;
import com.zcf.game_util.Time_Room;
import com.zcf.service.impl.UserTableServiceImpl;
import com.zcf.util.System_Mess;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author guolele
 * @date 2019年2月20日 下午3:02:10
 *
 */
@ServerEndpoint("/game/{userid}")
public class PK_WebSocket {
    // json转换
    private Gson gson = new Gson();
    public Session session;
    // 用户信息
    public UserBean userBean;
    // 自己进入的房间
    private RoomBean rb;
    // 游戏逻辑类
    private GameService gs = new GameService();
    private Map<String, Object> returnMap = new HashMap<String, Object>();

    //用户数据处理
    private UserTableServiceImpl us = (UserTableServiceImpl) ContextLoader.getCurrentWebApplicationContext().getBean(
            "userService");

    /**
     * 打开连接
     */
    @OnOpen
    public void onOpen(@PathParam("userid") Integer userid, Session session) throws IOException {
        boolean bool = true;
        System.out.println("id----------------------" + userid);
        if (userid != null) {
            // 查询用户信息
            userBean = us.selectByid(Long.valueOf(userid));
            if (userBean != null) {
                userBean.setSession(session);
                // 将自己放入客户端集合
                Public_State.clients.put(String.valueOf(userBean.getUserid()), this);
                this.session = session;
                //selectRooms(null);
                System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接");
                bool = false;
            }
        }
        // 如果没走正常业务则归类非法连接
        if (bool) {
            session.close();
            System_Mess.system_Mess.ToMessagePrint("非法连接");
        }
    }

    /**
     * 关闭连接
     *
     */
    @OnClose
    public void onClose() throws IOException {
        if (userBean != null) {
            // 如果已加入房间则通知其他人自己退出
            if (rb != null) {
                // 标识已经掉线
                userBean.setGametype(2);
                returnMap.put("state", "109");// 掉线
                returnMap.put("id", userBean.getUserid());
                returnMap.put("type", "exitGame");
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "断开连接");
        }
    }

    /**
     * 接收消息
     *
     * @throws InterruptedException
     */
    @OnMessage
    public void onMessage(String msg) {

        returnMap.clear();
        System_Mess.system_Mess.ToMessagePrint("接收" + userBean.getNickname() + "的消息" + msg);
        Map<String, String> jsonTo = gson.fromJson(msg, new TypeToken<Map<String, String>>() {
        }.getType());

        // 加入房间
        if ("Matching".equals(jsonTo.get("type"))) {
            if (Public_State.PKMap.get(String.valueOf(jsonTo.get("room_number"))) == null) {
                returnMap.put("state", "-1");// 房间不存在
                returnMap.put("msg", "房间不存在");
                returnMap.put("type", "Matching");
                sendMessageTo(returnMap);
            } else {
                rb = Public_State.PKMap.get(String.valueOf(jsonTo.get("room_number")));
                if (userBean.getMoney() < rb.getJion_fen()) {
                    returnMap.put("state", "-1");// 金币不足
                    returnMap.put("msg", "金币不足");
                    returnMap.put("type", "Matching");
                    sendMessageTo(returnMap);
                } else {
                    try {
                        rb = gs.Matching(jsonTo, userBean);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 把自己信息推送给房间内其他玩家
                    returnMap.put("state", "0");// 加入房间成功
                    returnMap.put("type", "Matching");// 加入房间成功
                    returnMap.put("userid", userBean.getUserid());
                    rb.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,
                            "room_number-fen-user_positions-max_number-game_number-di_fen-foundation-room_type");
                    //sendMessageToAll(returnMap, rb);
                    sendMessageTo(returnMap);
                }
            }
            returnMap.clear();
        }

        // 坐下
        if ("Sit_down".equals(jsonTo.get("type"))) {
            int down = gs.Sit_down(userBean, rb);
            if (down == 0) {
                returnMap.put("state", "0"); // 坐下成功
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("type", "Sit_down");
                returnMap.put("user_positions", rb.getUser_positions());
                Room_change(rb,0);
            } else {
                returnMap.put("type", "Sit_down");
                returnMap.put("state", "-1"); // 坐下失败
                returnMap.put("msg", "坐下失败");
            }
            sendMessageTo(returnMap);
            sendMessageToAll(returnMap, rb);
            returnMap.clear();
            //第二个人坐下启动开始游戏线程
            if (rb.getGame_userList(0).size() == 2) {
                new Time_Room(rb, gs).start();
            }
            Room_change(rb,0);
        }

        // 抢庄
        if ("Robbery".equals(jsonTo.get("type"))) {
            if (userBean.getMoney() < rb.getJion_fen()) {
                returnMap.put("state", "-1");// 金币不足 抢庄失败
                returnMap.put("msg", "金币不足，抢庄失败");// 金币不足 抢庄失败
            } else {
                int branker_ord = Integer.valueOf(jsonTo.get("branker_ord"));
                if (rb.getBranker_ord() < branker_ord) {
                    rb.setBranker_ord(branker_ord);
                }
                userBean.setRobbery(branker_ord);
                returnMap.put("state", "0");// 抢庄操作成功
            }
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("type", "Robbery");
            sendMessageTo(returnMap);
            sendMessageToAll(returnMap, rb);
            returnMap.clear();
        }

        // 获取观战列表
        if ("Guanzhan".equals(jsonTo.get("type"))) {
            List<UserBean> list = gs.getGuanZhan(rb);
            returnMap.put("type", "Guanzhan");
            returnMap.put("guanzhan_list", list);
            returnMap.put("state", "0");
            sendMessageTo(returnMap);
        }

        // 闲家下注
        if ("Xian_ord".equals(jsonTo.get("type"))) {
            int xian_ord = Integer.valueOf(jsonTo.get("xian_ord"));
            userBean.setOdd(xian_ord);
            returnMap.put("state", "0");// 抢庄操作成功
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("type", "xian_ord");
            returnMap.put("ord", xian_ord);
            sendMessageTo(returnMap);
            sendMessageToAll(returnMap, rb);
            returnMap.clear();
        }
        if ("Select_rooms".equals(jsonTo.get("type"))) {
            Double fen_type = Double.valueOf(jsonTo.get("fen_type"));
            selectRooms(fen_type);
        }

        // 开牌
        if ("Open_brand".equals(jsonTo.get("type"))) {
            int count = 0;
            int open = 0;
            userBean.setOpen_brand(1);
            System.out.println("开牌用户id:" + userBean.getUserid());
            System.out.println("开牌状态：" + userBean.getOpen_brand());
            for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                if (rb.getGame_userList(0).get(i).getOpen_brand() == 1) {
                    open++;
                }
                if (rb.getGame_userList(0).get(i).getGametype() == 1) {
                    count++;
                }
            }

            if (open == count) {
                returnMap.put("type", "Open_brand");
                returnMap.put("brand", userBean.getBrand());
                returnMap.put("id", userBean.getUserid());
                sendMessageTo(returnMap);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
            } else {
                returnMap.put("type", "Open_brand");
                returnMap.put("brand", userBean.getBrand());
                returnMap.put("id", userBean.getUserid());
                sendMessageTo(returnMap);
                sendMessageToAll(returnMap, rb);
            }
        }

        // 退出房间
        if ("Exit_room".equals(jsonTo.get("type"))) {
            if (rb.getGame_userList(0).size() == 0) {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (rb != null && rb.getGame_userList(0).size() > 1) {
                rb.getRoom_Branker(rb, userBean);
                // returnMap.put("positions", rb.getUser_positions());
                returnMap.put("id", userBean.getUserid());
                returnMap.put("state", "1");// 房间还有别人的情况
                returnMap.put("type", "Exit_room");
                returnMap.put("room_branker", rb.getRoom_branker());
                sendMessageTo(returnMap);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                rb.Exit_Room(rb, userBean);
            } else {
                returnMap.put("id", userBean.getUserid());
                returnMap.put("state", "0");// 房间没人 直接解散
                returnMap.put("type", "Exit_room");
                sendMessageTo(returnMap);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                    Public_State.clients.remove(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
                }
                Public_State.PKMap.remove(String.valueOf(rb.getRoom_number()));
            }
        }
    }

    private void selectRooms(Double di_fen) {
        returnMap.clear();
        RoomBean r = new RoomBean();
        List<Map<String, Object>> maps = r.getrooms(di_fen, "room_type-di_fen-jion_fen-foundation-person_num",
                "avatarurl");
        returnMap.put("rooms", maps);
        returnMap.put("type", "Select_rooms");
        for (String s : Public_State.clients.keySet()) {
            if (Public_State.clients.get(s).userBean.getUsertype() == 0) {//证明是没在游戏中
                PK_WebSocket webSocket = Public_State.clients.get(s);
                if (webSocket != null && webSocket.session.isOpen()) {
                    webSocket.sendMessageTo(returnMap);
                }
            }
        }
    }

    private void Room_change(RoomBean room, int type) {
        returnMap.clear();
        returnMap.put("type", "Room_change");
        returnMap.put("change_type", type);
        room.getRoomBean_Custom("avatarurl", returnMap, "room_number-game_number");

        for (Map.Entry<String, PK_WebSocket> entry : Public_State.clients.entrySet()) {
            PK_WebSocket ws = entry.getValue();
            if (ws.userBean.getUsertype()==0) {//证明不在游戏中
                ws.sendMessageTo(returnMap);
            }
        }
    }

    // 消息异常
    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        if (error.getMessage() != null) {
            error.printStackTrace();
            System_Mess.system_Mess
                    .ToMessagePrint(userBean.getNickname() + "异常" + error.getLocalizedMessage() + "***********");
        }
    }

    /**
     * 退出房间 清除自己
     */

    private void Exit_Room(int type) {
        rb.getLock().lock();
        if (type == 1) {
            Public_State.PKMap.remove(rb.getRoom_number());
            System_Mess.system_Mess.ToMessagePrint("房间清除");
            rb = null;
        } else {
            userBean.setGametype(2);
            returnMap.put("id", userBean.getUserid());
            returnMap.put("state", "109");
            returnMap.put("type", "exitGame");
            sendMessageTo(returnMap);
            sendMessageToAll(returnMap, rb);
        }
        rb.getLock().unlock();
    }

    /**
     * 发送消息给除去自己以外的所有人
     *
     * @throws IOException
     */
    public synchronized void sendMessageToAll(Map<String, Object> returnMap, RoomBean rb) {
        // String returnjson = gson.toJson(returnMap).toString(); //
        // RC4.encry_RC4_string(gson.toJson(returnMap).toString());;
        for (UserBean user : rb.getGame_userList(1)) {
            PK_WebSocket webSocket = Public_State.clients.get(user.getUserid() + "");
            if (webSocket != null && webSocket.session.isOpen()) {
                // 不等于自己的则发送消息
                if (user.getUserid() != this.userBean.getUserid()) {
                    webSocket.sendMessageTo(returnMap);
                }
            }
        }
    }

    /**
     * 给自己返回消息
     *
     * @throws IOException
     *
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap) {
        if (session != null && session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            try {
                session.getBasicRemote().sendText(returnjson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getUserid() + "(自己)返回消息" + returnjson);
        }
    }

}

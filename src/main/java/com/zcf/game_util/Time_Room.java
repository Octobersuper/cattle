package com.zcf.game_util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.game_center.PK_WebSocket;
import com.zcf.util.System_Mess;

/**
 * @author
 * @date
 */
public class Time_Room extends Thread {
    GameService gs;
    RoomBean rb;
    int timer = 0;

    public Time_Room(UserBean userBean, RoomBean rb, GameService gs) {
        this.rb = rb;
        this.timer = 0;
        this.gs = gs;
    }

    HashMap<String, Object> returnMap = new HashMap<String, Object>();

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (rb == null) {
                break;
            }
            timer--;
            rb.setTimes(timer);
            System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);

            if (timer == 25) {
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    returnMap.put("state", "0");
                    returnMap.put("type", "start_game");
                    returnMap.put("timer", timer);
                    rb.setBrands(1);
                    socket.sendMessageTo(returnMap);
                    socket.sendMessageToAll(returnMap, rb);
                    returnMap.clear();
                }
            }

            // 24秒开始发牌
            if (timer == 24) {
                // 发牌
                rb.GrantBrand(5, rb);
                rb.setRoom_state(2);
                returnMap.put("state", "0");
                returnMap.put("type", "sendCards");
                // returnMap.put("brand_list", rb.getRb_List());
                rb.getRoomBean_Custom("userid-brand-user_brand_type", returnMap, "");
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
                returnMap.clear();
            }

            //自定选庄阶段
            if (timer == 19) {
                rb.selectBranker_id(rb);
                returnMap.put("state", "0");
                returnMap.put("type", "getBranker");
                returnMap.put("branker_id", rb.getBranker_id());
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
                returnMap.clear();
            }

            //闲家加倍阶段
            if (timer == 14) {

            }
            //开牌阶段
            if (timer == 10) {

            }

            // 结算
            if (timer == 5) {
                for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                    if (rb.getGame_userList(0).get(i).getUserid() != rb.getBranker_id()) {
                        UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
                        gs.EndGame(rb, bean);
                    }
                }
                returnMap.put("type", "account");
                rb.getRoomBean_Custom("userid-money-winnum-brand_index", returnMap, "");
                //returnMap.put("list", list);
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
                returnMap.clear();
            }

            // 时间线程结束则更改房间状态并且开始下一局
            if (timer == 0) {
                if (rb.getGame_number() < rb.getMax_number()) {
                    rb.setRoom_state(0);
                    rb.setGame_number(rb.getGame_number() + 1);
                    rb.setBranker_ord(0);
                    rb.setBranker_id(0);
                    for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                        if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
                            rb.getGame_userList(0).get(i).Initialization();
                            rb.getGame_userList(0).get(i).setUsertype(1);
                        }
                    }
                    rb.setOrds(1);
                    // break;
                }
            }
        }

        System_Mess.system_Mess.ToMessagePrint("开始游戏线程结束");

    }
}

package com.zcf.game_util;

import java.util.HashMap;

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

    public Time_Room(RoomBean rb, GameService gs) {
        this.rb = rb;
        this.timer = 30;
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
            //如果人数不足两人 countune
            if(rb.getGame_userList(0).size()<2){
                System_Mess.system_Mess.ToMessagePrint("人数不足----");
                timer = 30;
                continue;
            }
            if(timer==30){//倒计时五秒开始
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    returnMap.put("state", "0");
                    returnMap.put("type", "Start_game");
                    returnMap.put("timer", 5);
                    rb.setBrands(1);
                    socket.sendMessageTo(returnMap);
                    socket.sendMessageToAll(returnMap, rb);
                    returnMap.clear();
                }
            }

            //抢庄阶段 选出庄家
            if (timer == 20) {
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
            if (timer == 15) {
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    break;
                } else {
                    for (UserBean user :
                            rb.getGame_userList(0)) {
                        if (user.getOdd()==0) {
                            user.setOdd(1);
                            returnMap.put("state", "0");// 抢庄操作成功
                            returnMap.put("userid", user.getUserid());
                            returnMap.put("type", "xian_ord");
                            returnMap.put("ord", 1);
                        }
                    }
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
            }

            // 开始发牌
            if (timer == 15) {
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

            //判断是否所有人都开牌  都开牌就直接开牌
            if(timer<15 && timer>8){
                if(rb.getUserBrandState()){
                    returnMap.put("state", "0");
                    returnMap.put("type", "openCards");
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
                    timer = 7;
                }
            }
            //开牌阶段
            if (timer == 8) {
                returnMap.put("state", "0");
                returnMap.put("type", "openCards");
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

            // 结算
            if (timer == 5) {
                for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                    if (rb.getGame_userList(0).get(i).getUserid() != rb.getBranker_id()) {
                        UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
                        gs.EndGame(rb, bean);
                    }
                }
                returnMap.put("state", "0");
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
                    timer = 30;
                }
            }

            timer--;
            rb.setTimes(timer);
            System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);
        }
        System_Mess.system_Mess.ToMessagePrint("开始游戏线程结束");

    }
}

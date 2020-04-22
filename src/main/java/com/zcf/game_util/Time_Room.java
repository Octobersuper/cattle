package com.zcf.game_util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.game_center.PK_WebSocket;
import com.zcf.pojo.PkRecordTable;
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
            if (rb == null) {
                System_Mess.system_Mess.ToMessagePrint("房间清除，跳出线程----");
                break;
            }
            //如果人数不足两人 countune
            if (timer <= 30 && timer >= 25) {
                rb.setRoom_state(0);
                //如果人数不足两人 countune
                if (rb.getGame_userList(0).size() < 2) {
                    System_Mess.system_Mess.ToMessagePrint("人数不足----");
                    timer = 30;
                    break;
                }
            }
            if (timer == 30) {//倒计时五秒开始
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    continue;
                } else {
                    returnMap.put("state", "0");
                    returnMap.put("type", "Start_game");
                    returnMap.put("timer", 10);
                    socket.sendMessageTo(returnMap);
                    socket.sendMessageToAll(returnMap, rb);
                    returnMap.clear();
                }
            }

            if (timer == 25) {
                rb.setRoom_state(1);
            }

            //抢庄阶段 选出庄家
            if (timer == 20) {
                rb.selectBranker_id(rb);
                returnMap.put("state", "0");
                returnMap.put("type", "getBranker");
                returnMap.put("branker_id", rb.getBranker_id());
                returnMap.put("timer", 5);
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    continue;
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
                    continue;
                } else {
                    for (UserBean user :
                            rb.getGame_userList(0)) {
                        if (user.getOdd() == 0) {
                            user.setOdd(1);
                            returnMap.put("state", "0");
                            returnMap.put("userid", user.getUserid());
                            returnMap.put("type", "xian_ord");
                            returnMap.put("xian_ord", 1);
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap);
                        }
                    }
                }
            }

            // 开始发牌
            if (timer == 13) {
                // 发牌
                rb.GrantBrand(5, rb);
                rb.setRoom_state(2);
                returnMap.put("state", "0");
                returnMap.put("timer", 5);
                returnMap.put("type", "sendCards");
                // returnMap.put("brand_list", rb.getRb_List());
                rb.getRoomBean_Custom("userid-brand-user_brand_type", returnMap, "");
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    continue;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
                returnMap.clear();
            }

            //判断是否所有人都开牌  都开牌就直接开牌
            if (timer < 13 && timer > 5) {
                if (rb.getUserBrandState()) {
                    timer = 4;
                }
            }
            //开牌阶段
            if (timer == 5) {
                for (UserBean user :
                        rb.getGame_userList(0)) {
                    if (user.getOpen_brand() != 1) {
                        returnMap.put("state", "0");
                        returnMap.put("type", "Open_brand");
                        returnMap.put("user_brand_type", user.getUser_brand_type());
                        returnMap.put("brand_index", user.getBrand_index());
                        returnMap.put("brand", user.getBrand());
                        returnMap.put("userid", user.getUserid());
                        PK_WebSocket socket = Public_State.getPkWebSocket();
                        if (socket == null) {
                            break;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap);
                        }
                    }
                }
                returnMap.clear();
            }

            // 结算
            if (timer == 4) {
                for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                    if (rb.getGame_userList(0).get(i).getUserid() != rb.getBranker_id()) {
                        UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
                        gs.EndGame(rb, bean);
                        //上局回顾
                        String re = "";
                        for (int j = 0; j < bean.getBrand().length; j++) {
                            if (j == 0) {
                                re = re + bean.getBrand()[j];
                            } else {
                                re = re + "-" + bean.getBrand()[j];
                            }
                        }
                        re = re + "/" + bean.getUser_brand_type() + "/" + bean.getOdds() + "/" + bean.getWinnum();
                        bean.setReview(re);
                        //插入战绩
                        PkRecordTable pk = new PkRecordTable();
                        pk.setCreatedate(new Date());
                        pk.setUserid(bean.getUserid());
                        pk.setMoney(bean.getMoney());
                        pk.setNumber(bean.getWinnum());
                        pk.insert();
                    }
                }
                //插入庄家
                UserBean userBean = rb.getUserBean(rb.getBranker_id());
                //上局回顾
                String re = "";
                for (int j = 0; j < userBean.getBrand().length; j++) {
                    if (j == 0) {
                        re = re + userBean.getBrand()[j];
                    } else {
                        re = re + "-" + userBean.getBrand()[j];
                    }
                }
                re = re + "/" + userBean.getUser_brand_type() + "/" + userBean.getOdds() + "/" + userBean.getWinnum();
                userBean.setReview(re);
                //插入战绩
                PkRecordTable pk = new PkRecordTable();
                pk.setCreatedate(new Date());
                pk.setUserid(userBean.getUserid());
                pk.setMoney(userBean.getMoney());
                pk.setNumber(userBean.getWinnum());
                pk.insert();

                returnMap.put("state", "0");
                returnMap.put("type", "account");
                rb.getRoomBean_Custom("userid-money-winnum-brand_index", returnMap, "");
                //returnMap.put("list", list);
                PK_WebSocket socket = Public_State.getPkWebSocket();
                if (socket == null) {
                    continue;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap);
                }
                returnMap.clear();
            }

            // 时间线程结束则更改房间状态并且开始下一局
            if (timer == 0) {
                //剔除掉线玩家
                List<UserBean> list1 = rb.getGame_userList(0);
                for (UserBean user : list1) {
                    if (user.getMoney() <= 0) {
                        returnMap.put("userid", user.getUserid());
                        returnMap.put("state", "0");
                        returnMap.put("type", "No_money");
                        PK_WebSocket socket = Public_State.getPkWebSocket();
                        if (socket == null) {
                            break;
                        } else {
                            socket.sendMessageTo(returnMap);
                        }
                        returnMap.clear();
                    }
                    if (user.getGametype() == 2 || user.getMoney() <= 0) {
                        returnMap.put("userid", user.getUserid());
                        returnMap.put("state", "0");
                        returnMap.put("type", "Exit_room");
                        PK_WebSocket socket = Public_State.getPkWebSocket();
                        if (socket == null) {
                            break;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                        }
                        rb.getGame_userList().remove(user);
                        rb.remove_options(user.getUserid());
                        returnMap.clear();
                        Public_State.clients.remove(String.valueOf(user.getUserid()));
                    }
                }
                rb.Initialization();
                timer = 30;
            }

            timer--;
            rb.setTimes(timer);
            System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);
        }

    }
}

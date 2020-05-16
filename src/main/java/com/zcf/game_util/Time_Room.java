package com.zcf.game_util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
        this.timer = 40;
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
            if (timer <= 40 && timer >= 35) {
                rb.setRoom_state(0);
                //如果人数不足两人 countune
                if (rb.getGame_userList(0).size() < 2) {
                    System_Mess.system_Mess.ToMessagePrint("人数不足----");
                    timer = 40;
                    break;
                }
            }
            if (timer == 40) {//倒计时五秒开始
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    returnMap.put("state", "0");
                    returnMap.put("type", "Start_game");
                    returnMap.put("timer", 10);
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                    returnMap.clear();

                    //发牌  发三张
                    rb.GrantBrand(5, rb);
                    returnMap.put("state", "0");
                    returnMap.put("type", "SendCards");
                    // returnMap.put("brand_list", rb.getRb_List());
                    rb.getRoomBean_Custom("userid-brand3-user_brand_type", returnMap, "");
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                    returnMap.clear();
                }
                rb.setRoom_state(1);
            }

            if(timer==33){//机器人自动抢庄
                List<UserBean> list = rb.getGame_userList(0);
                for (UserBean userBean:list) {
                    if(userBean.getIsAi()!=0){
                        int branker_ord = new Random().nextInt(6);//随机数
                        returnMap.put("state", "0");
                        returnMap.put("userid", userBean.getUserid());
                        returnMap.put("branker_ord", String.valueOf(branker_ord));
                        returnMap.put("type", "Robbery");
                        if (rb.getBranker_ord() < branker_ord) {
                            rb.setBranker_ord(branker_ord);
                        }
                        userBean.setRobbery(branker_ord);
                        PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                        if (socket == null) {
                            timer = 41;
                            rb.removeUsers();
                            continue;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                    }
                }
            }

            if(timer==30){//玩家自动抢庄
                returnMap.clear();
                List<UserBean> list = rb.getGame_userList(0);
                for (UserBean userBean:list) {
                    if(userBean.getIsAi()==0 && userBean.getRobbery()==-1){//普通玩家
                        returnMap.put("state", "0");
                        returnMap.put("userid", userBean.getUserid());
                        returnMap.put("branker_ord", "0");
                        returnMap.put("type", "Robbery");
                        PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                        if (socket == null) {
                            timer = 41;
                            rb.removeUsers();
                            continue;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                    }
                }
            }

            //抢庄阶段 选出庄家
            if (timer == 29) {
                returnMap.clear();
                rb.selectBranker_id(rb);
                returnMap.put("state", "0");
                returnMap.put("type", "GetBranker");
                returnMap.put("branker_id", rb.getBranker_id());
                returnMap.put("timer", 5);
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                }
                returnMap.clear();
            }
            //机器人闲家加倍阶段
            if(timer==26){
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    int[] odds = {1,5,10,15,20};
                    List<UserBean> list = rb.getGame_userList(0);
                    for (UserBean user :list) {
                        if (user.getOdd() == 0 && user.getIsAi()==1 &&rb.getBranker_id()!=user.getUserid()) {
                            int ord = new Random().nextInt(5);//随机数
                            ord = odds[ord];
                            user.setOdd(ord);
                            returnMap.put("state", "0");
                            returnMap.put("userid", user.getUserid());
                            returnMap.put("type", "Xian_ord");
                            returnMap.put("xian_ord", ord);
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                    }
                }
            }

            //普通闲家加倍阶段
            if (timer == 24) {
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    List<UserBean> list = rb.getGame_userList(0);
                    for (UserBean user :list) {
                        if (user.getOdd() == 0 && user.getIsAi()==0) {
                            user.setOdd(1);
                            returnMap.put("state", "0");
                            returnMap.put("userid", user.getUserid());
                            returnMap.put("type", "Xian_ord");
                            returnMap.put("xian_ord", 1);
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                    }
                }
            }

            // 开始发牌  发两张
            if (timer == 23) {
                rb.setRoom_state(2);
                returnMap.put("state", "0");
                returnMap.put("timer", 10);
                returnMap.put("type", "SendCards");
                // returnMap.put("brand_list", rb.getRb_List());
                rb.getRoomBean_Custom("userid-brand2-user_brand_type", returnMap, "");
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                }
                returnMap.clear();
            }

            //判断是否所有人都开牌  都开牌就直接开牌
            if (timer < 23 && timer > 13) {
                if (rb.getUserBrandState()) {
                    timer = 12;
                }
            }
            //开牌阶段
            if (timer == 13) {
                List<UserBean> list = rb.getGame_userList(0);
                for (UserBean user :
                        list) {
                    if (user.getOpen_brand() != 1) {
                        returnMap.put("state", "0");
                        returnMap.put("type", "Open_brand");
                        returnMap.put("user_brand_type", user.getUser_brand_type());
                        returnMap.put("odds", user.getOdds());
                        returnMap.put("brand_index", user.getBrand_index());
                        returnMap.put("brand", user.getBrand());
                        returnMap.put("userid", user.getUserid());
                        PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                        if (socket == null) {
                            timer = 41;
                            rb.removeUsers();
                            continue;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                    }
                }
                returnMap.clear();
            }

            // 结算
            if (timer == 12) {
                List<UserBean> list = rb.getGame_userList(0);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setPlayNumber(list.get(i).getPlayNumber()+1);
                    if (list.get(i).getUserid() != rb.getBranker_id()) {
                        UserBean bean = rb.getUserBean(list.get(i).getUserid());
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
                returnMap.put("type", "Account");
                rb.getRoomBean_Custom("userid-money-winnum-brand_index", returnMap, "");
                //returnMap.put("list", list);
                PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                if (socket == null) {
                    timer = 41;
                    rb.removeUsers();
                    continue;
                } else {
                    socket.sendMessageToAll(returnMap, rb);
                    socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                }
                returnMap.clear();
                rb.setRoom_state(0);
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
                        PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                        if (socket == null) {
                            timer = 41;
                            rb.removeUsers();
                            continue;
                        } else {
                            socket.sendMessageTo(returnMap,user.getUserid());
                        }
                        returnMap.clear();
                        socket.Room_change(rb, 0);
                    }
                    if (user.getGametype() == 2 || user.getMoney() <= 0) {
                        returnMap.put("userid", user.getUserid());
                        returnMap.put("state", "0");
                        returnMap.put("type", "Exit_room");
                        PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                        if (socket == null) {
                            timer = 41;
                            rb.removeUsers();
                            continue;
                        } else {
                            socket.sendMessageToAll(returnMap, rb);
                            socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                        }
                        rb.getGame_userList().remove(user);
                        rb.remove_options(user.getUserid());
                        returnMap.clear();
                        Public_State.clients.remove(String.valueOf(user.getUserid()));
                        socket.Room_change(rb, 0);
                    }
                    if(user.getGametype()==3){//观战机器人 设置成参战
                        user.setGametype(1);
                        int down = gs.Sit_down(user, rb);
                        if (down == 0) {
                            returnMap.put("state", "0"); // 坐下成功
                            returnMap.put("userid", user.getUserid());
                            returnMap.put("type", "Sit_down");
                            returnMap.put("user_positions", rb.getUser_positions());
                            rb.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,"");
                            PK_WebSocket socket = Public_State.getPkWebSocket(rb.getRoom_number());
                            if (socket == null) {
                                timer = 41;
                                rb.removeUsers();
                                continue;
                            } else {
                                socket.sendMessageToAll(returnMap, rb);
                                socket.sendMessageTo(returnMap,socket.userBean.getUserid());
                            }
                            socket.Room_change(rb, 0);
                        }
                    }
                }
                rb.Initialization();
                timer = 41;/*
                count++;
                if(count==3){
                    break;
                }*/
            }

            timer--;
            rb.setTimes(timer);
            System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);
        }

    }
}

/**
 * 
 */
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
 * @author guolele
 * @date 2019年3月8日 上午10:46:07
 * 
 */
public class Time_Room extends Thread {
	GameService gs;
	RoomBean rb;
	int timer = 0;
	UserBean userBean;

	public Time_Room(UserBean userBean, RoomBean rb, GameService gs) {
		this.userBean = userBean;
		this.rb = rb;
		this.timer = 0;
		this.gs = gs;
	}

	public void setUser(UserBean userBean) {
		this.userBean = userBean;
		this.timer = 0;
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
			if (rb.getTimes() == 30) {
				timer = 30;
				returnMap.put("type", "sit_down_time");
				returnMap.put("timer", timer);
				returnMap.put("game_number", rb.getGame_number());
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
					UserBean userBean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
					userid.sendMessageTo(returnMap);
				}
				returnMap.clear();

			}
			int count = 0;
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				int id = rb.getGame_userList(0).get(i).getUserid();
				UserBean user = rb.getUserBean(id);
				if (user.getMoney() < 20 && user.getUsertype() == 1) {
					user.setGametype(-1);
					user.setUsertype(0);
					for (int j = 0; j < rb.getUser_positions().length; j++) {
						if (rb.getUser_positions()[i] == user.getUserid()) {
							rb.getUser_positions()[i] = -1;// 金币不足 设置成观战
						}
					}
					PK_WebSocket userid = Public_State.clients.get(String.valueOf(id));
					returnMap.put("id", id);
					returnMap.put("type", "newStart");
					returnMap.put("state", "107"); //金币不足
					userid.sendMessageTo(returnMap);
				}
				if (user.getUsertype() == 1 && user.getGametype() != 2 || user.getGametype() != -1) {
					count++;
				}
			}
			if (rb.getRoom_state() != 2 || rb.getRoom_type() != 0 || count < 2) {
				rb.setRoom_state(0);
				continue;
			}
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
					for (int j = 0; j < rb.getUser_positions().length; j++) {
						UserBean bean = rb.getUserBean(rb.getRoom_branker());
						if (bean.getUserid() == rb.getUser_positions()[j] && bean.getGametype() == -1 || bean.getGametype() == 2) {
							rb.setRoom_branker(rb.getUser_positions()[j + 1]);
						}
					}
				}
			}
			
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
					rb.getGame_userList(0).get(i).setGametype(1);
				}
			}
			
			if (timer < 15) {
				timer = rb.getTimes();
			}
			timer--;
			rb.setTimes(timer);
			System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);

			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				rb.getGame_userList(0).get(i).setGametype(1);
			}

			if (timer == 25) {
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				returnMap.put("type", "start_game");
				returnMap.put("timer", timer);
				// returnMap.put("game_number", rb.getGame_number());
				// 如果符合开始游戏条件则开始游戏 更改房间状态
				rb.setBrands(1);
				userid.sendMessageTo(returnMap);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();
			}

			

			// 24秒开始发牌
			if (timer == 24) {
				// 发牌
				rb.GrantBrand(5, rb);
				rb.setRoom_state(2);
				returnMap.put("type", "fapai");
				// returnMap.put("brand_list", rb.getRb_List());
				rb.getRoomBean_Custom("userid-brand-user_brand_type", returnMap, "");
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				userid.sendMessageToAll(returnMap, rb);
				userid.sendMessageTo(returnMap);
				returnMap.clear();
			}
			
			if (timer == 19) {
				rb.selectBranker_id(rb);
				returnMap.put("type", "getBranker");
				returnMap.put("branker_id", rb.getBranker_id());
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				userid.sendMessageTo(returnMap);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();
			}
			
			if (timer == 14) {
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					if (rb.getGame_userList(0).get(i).getUsertype() == 1 && rb.getGame_userList(0).get(i).getOdd() == 1 && rb.getGame_userList(0).get(i).getUserid() != rb.getBranker_id()) {
						rb.getGame_userList(0).get(i).setOdd(1);
					}
				}
				/*returnMap.put("type", "open_brand_state");
				returnMap.put("timer", timer);
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				userid.sendMessageTo(returnMap, userBean);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();*/
			}
			

			// 结算
			if (timer == 5) {
				rb.setTimes(5);
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					if (rb.getGame_userList(0).get(i).getUserid() != rb.getBranker_id()&& rb.getGame_userList(0).get(i).getUsertype() ==1) {
						UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
						List<Map<String, Object>> list = gs.EndGame(rb, bean);
					}
				}
				returnMap.put("type", "account");
				rb.getRoomBean_Custom("userid-money-winnum-brand_index", returnMap, "");
				//returnMap.put("list", list);
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				
				UserBean bean = rb.getUserBean(rb.getBranker_id());
				userid.sendMessageTo(returnMap);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();
			}
			if (timer == 5 && rb.getGame_number() == rb.getMax_number()) {
				returnMap.put("type", "zhanji");
				rb.getRoomBean_Custom("userid-nickname-win_money", returnMap, "");
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				userid.sendMessageTo(returnMap);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();
			}

			/*if (timer == 3) {
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
					if (bean.getUsertype() == 1) {
						bean.setWinnum(bean.getMoney() - bean.getMoney_a());
					}
				}
				returnMap.put("type", "winnum");
				rb.getRoomBean_Custom("userid-winnum", returnMap, "");
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getBranker_id()));
				userid.sendMessageTo(returnMap, userBean);
				userid.sendMessageToAll(returnMap, rb);
				returnMap.clear();
			}*/
			
			if (timer == 3 && rb.getGame_number() == rb.getMax_number()) {
				PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
				returnMap.put("type", "zhanji");
				rb.getRoomBean_Custom("userid-nickname-win_money", returnMap, "");
				userid.sendMessageTo(returnMap);
				userid.sendMessageToAll(returnMap, rb);
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
					/*// 删除庄家
					for (int i = 0; i < rb.getRb_List().size(); i++) {
						if (rb.getRb_List().get(i).getUserid() == rb.getBranker_id()) {
							rb.getRb_List().remove(i);
						}
					}*/
					gs.Game_Start(rb, userBean);
					System_Mess.system_Mess.ToMessagePrint("开始游戏跳出线程");
					// break;
				} else {
					returnMap.put("type", "gameover");
					PK_WebSocket userid = Public_State.clients.get(String.valueOf(rb.getRoom_branker()));
					userid.sendMessageToAll(returnMap, rb);
					userid.sendMessageTo(returnMap);
					for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
						UserBean user = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
						Public_State.clients.remove(user);
					}
					Public_State.PKMap.remove(rb.getRoom_number());
					Public_State.PKMap.remove(rb.getRoom_number(), rb);
					break;
				}
			}
		}

		System_Mess.system_Mess.ToMessagePrint("开始游戏线程结束");

	}
}

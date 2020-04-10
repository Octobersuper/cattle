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
	private UserTableServiceImpl us=(UserTableServiceImpl) ContextLoader.getCurrentWebApplicationContext().getBean("userService");
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
		// 创建房间
		if ("CreateRoom".equals(jsonTo.get("type"))) {
			if (userBean.getMoney() < Integer.valueOf(jsonTo.get("fen"))) {
				returnMap.put("state", "101");// 金币不足
				returnMap.put("type", "CreateRoom");// 创建房间
				returnMap.put("userid", userBean.getUserid());
				sendMessageTo(returnMap);
			} else {
				rb = gs.Esablish(jsonTo, userBean);
				returnMap.put("state", "102");// 创建房间成功
				returnMap.put("type", "CreateRoom");// 创建房间
				returnMap.put("userid", userBean.getUserid());
				rb.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,
						"room_number-fen-user_positions-max_number-game_number-di_fen-foundation-room_type");
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
			}
		}

		// 加入房间
		if ("Matching".equals(jsonTo.get("type"))) {

			if (Public_State.PKMap.get(String.valueOf(jsonTo.get("room_number"))) == null) {
				returnMap.put("state", "103");// 房间不存在
				returnMap.put("type", "Matching");
				sendMessageTo(returnMap);
			} else {
				rb = Public_State.PKMap.get(String.valueOf(jsonTo.get("room_number")));
				if (userBean.getMoney() < rb.getFen()) {
					returnMap.put("state", "107");// 金币不足
					returnMap.put("type", "Matching");
					sendMessageTo(returnMap);
				} else if (rb.getGame_userList(0).size() >= rb.getFoundation()) {
					returnMap.put("state", "104");// 房间已满
					returnMap.put("type", "Matching");
					sendMessageTo(returnMap);
				} else {
					try {
						rb = gs.Matching(jsonTo, userBean);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					userBean.setUsertype(-1);// 观战
					// 把自己信息推送给房间内其他玩家
					returnMap.put("state", "106");// 加入房间成功
					returnMap.put("type", "Matching");// 加入房间成功
					returnMap.put("userid", userBean.getUserid());
					rb.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,
							"room_number-fen-user_positions-max_number-game_number-di_fen-foundation-room_type");
					sendMessageToAll(returnMap, rb);
					sendMessageTo(returnMap);

				}
			}
			returnMap.clear();
		}

		// 坐下
		if ("Sit_down".equals(jsonTo.get("type"))) {
			int down = gs.Sit_down(userBean, rb);
			if (down == 0) {
				returnMap.put("state", "130"); // 坐下成功
				returnMap.put("room_branker", rb.getRoom_branker());
				returnMap.put("userid", userBean.getUserid());
				returnMap.put("type", "Sit_down");
				returnMap.put("user_positions", rb.getUser_positions());
			} else {
				returnMap.put("type", "Sit_down");
				returnMap.put("state", "131"); // 坐下失败
			}
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
			returnMap.clear();
		}

		// 抢庄
		if ("Robbery".equals(jsonTo.get("type"))) {
			int branker_ord = Integer.valueOf(jsonTo.get("branker_ord"));
			if (rb.getBranker_ord() < branker_ord) {
				rb.setBranker_ord(branker_ord);
			}
			userBean.setRobbery(branker_ord);
			if (userBean.getMoney() < 20) {
				returnMap.put("state", "107");// 金币不足 抢庄失败
			} else {
				returnMap.put("state", "110");// 抢庄操作成功
			}
			returnMap.put("userid", userBean.getUserid());
			returnMap.put("type", "Robbery");
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
			returnMap.clear();
		}

		// 观战
		if ("guanzhan".equals(jsonTo.get("type"))) {
			List<UserBean> list = gs.getGuanZhan(rb);
			returnMap.put("type", "guanzhan");
			returnMap.put("guanzhan_list", list);
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
		}

		// 闲家下注
		if ("xian_ord".equals(jsonTo.get("type"))) {
			int xian_ord = Integer.valueOf(jsonTo.get("xian_ord"));
			userBean.setOdd(xian_ord);
			if (userBean.getMoney() < 20) {
				returnMap.put("state", "107");// 金币不足 抢庄失败
			} else {
				returnMap.put("state", "110");// 抢庄操作成功
			}
			returnMap.put("userid", userBean.getUserid());
			returnMap.put("type", "xian_ord");
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
			returnMap.clear();
		}

		// 开始游戏
		if ("start_game".equals(jsonTo.get("type"))) {
			int start_Game = rb.Start_Game(userBean, rb);
			if (start_Game == 2) {
				returnMap.put("type", "start_game");
				returnMap.put("timer", rb.getTimer_user());
				returnMap.put("room_number", rb.getGame_number());
				returnMap.put("state", "2"); // 游戏开始成功
			} else {
				returnMap.put("type", "start_game");
				returnMap.put("state", "0"); // 准备人数不足 无法开始游戏
			}
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
			returnMap.clear();
		}

		// 开牌
		if ("open_brand".equals(jsonTo.get("type"))) {
			userBean.setOpen_brand(1);
			int open_Brand_Count = gs.Open_Brand_Count(rb);
			if (open_Brand_Count == 1) {
				rb.setTimer_user(6);
			}
			returnMap.put("type", "open_brand");
			returnMap.put("open_brand_count", open_Brand_Count); // 如果返回0则
																	// 没有全部亮牌
																	// 如果返回1则全部亮牌
			returnMap.put("userid", userBean.getUserid());
			sendMessageTo(returnMap);
			sendMessageToAll(returnMap, rb);
			returnMap.clear();
		}

		// 断线重连
		if ("reconnection".equals(jsonTo.get("type"))) {
			rb = Public_State.PKMap.get(jsonTo.get("room_number"));
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				if (userBean.getUserid() == rb.getGame_userList(0).get(i).getUserid()) {
					UserBean user = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
					userBean.setSession(user.getSession());
					user.setGametype(1);
				}
			}
			if (rb.getRoom_state_a() == 1 || rb.getRoom_state_a() == 2 || rb.getRoom_state_a() == 3) {
				rb.getRoomBean_Custom("id-wxname-wximg-money-gametype-brand-bets-betss-user_brand_type", returnMap,
						"branker_id-room_number-game_number-room_type-koupai-user_positions");
			} else {
				rb.getRoomBean_Custom("id-wxname-wximg-money-gametype-brand-bets-betss-user_brand_type", returnMap,
						"room_number-room_type-game_number-koupai-user_positions");
			}
			returnMap.put("timer", rb.getTimer_user());
			returnMap.put("type", "reconnection");
			returnMap.put("room_state_a", rb.getRoom_state_a());
			returnMap.put("game_number", rb.getGame_number());
			sendMessageTo(returnMap);
			returnMap.clear();
			returnMap.put("id", userBean.getUserid());
			returnMap.put("type", "reconnection");
			sendMessageToAll(returnMap, rb);
		}

		// 下注
		if ("bets".equals(jsonTo.get("type"))) {
			// 下注
			int bets = Integer.valueOf(jsonTo.get("bets"));
			String betss = String.valueOf(jsonTo.get("betss"));
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa:" + betss);
			int id = Integer.valueOf(jsonTo.get("id"));
			if (bets < 0) {
				bets = userBean.getOnbets();
			} else {
				userBean.setBetss(betss);
			}
			userBean.setBet(bets);
			userBean.setOnbets(bets);
			rb.setBets(bets);
			int money = 0;
			if (bets != 0) {
				money = bets;
			} else {
				money = rb.getMinBets();
			}
			returnMap.put("bets", userBean.getBet());
			// 继续游戏
			int state = gs.bets(money, userBean, rb);
			//int newMoney = userDao.getMoney(id);
			System_Mess.system_Mess.ToMessagePrint("(下注)房间状态" + state);
			// 金币不足 || 已结算
			if (state == 809) {
				jsonTo.put("state", "809");// 金钱不足
			} else if (state == 0) {
				userBean.setMoney(userBean.getMoney() - money);
				rb.getRoomBean_Custom("game_number", returnMap);
				if (userBean.getBetss() != null) {
					returnMap.put("betss", userBean.getBetss());
				}
				returnMap.put("type", "bets");
				returnMap.put("id", userBean.getUserid());
				//returnMap.put("money", newMoney * 100);
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
			}

		}

		// 开牌
		if ("OpenBrand".equals(jsonTo.get("type"))) {
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
				rb.setTimer_user(4);
				System.out.println("时间---------------》" + rb.getTimer_user());
				returnMap.put("type", "OpenBrand");
				returnMap.put("brand", userBean.getBrand());
				returnMap.put("id", userBean.getUserid());
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
				returnMap.clear();
			} else {
				returnMap.put("type", "OpenBrand");
				returnMap.put("brand", userBean.getBrand());
				returnMap.put("id", userBean.getUserid());
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
			}
		}

		// 发起解散
		if ("dissolve".equals(jsonTo.get("type"))) {
			int state = Integer.valueOf(jsonTo.get("dissolve_state"));
			int dissolve_userid = Integer.valueOf(jsonTo.get("dissolve_userid"));
			if (userBean.getUserid() == rb.getRoom_branker() && state == 1 && dissolve_userid == userBean.getUserid()
					&& rb.getRoom_state() == 0 && rb.getGame_number() == 1) { // 房主解散房间
				returnMap.put("type", "dissolve");
				returnMap.put("state", "400");// 房主解散房间
				returnMap.put("userid", userBean.getUserid());
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
				returnMap.clear();
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					Public_State.clients.remove(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
				}
				rb.setRoom_state(0);
				Public_State.PKMap.remove(String.valueOf(rb.getRoom_number()));
			} else {
				if (state == 1) {
					userBean.setJiesan(1);
					int jiesan = rb.getJiesan(rb, userBean);
					if (jiesan == 1) {
						returnMap.put("type", "dissolve");
						returnMap.put("state", "400");// 房间解散
						sendMessageTo(returnMap);
						sendMessageToAll(returnMap, rb);
						returnMap.clear();
						for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
							Public_State.clients.remove(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
						}
						rb.setRoom_state(0);
						Public_State.PKMap.remove(String.valueOf(rb.getRoom_number()));
					} else {
						returnMap.put("type", "dissolve");
						returnMap.put("state", "401");// 发起/同意解散
						returnMap.put("dissolve_userid", dissolve_userid);
						returnMap.put("userid", userBean.getUserid());
						sendMessageTo(returnMap);
						sendMessageToAll(returnMap, rb);
						returnMap.clear();
					}

				} else {
					for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
						if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
							rb.getGame_userList(0).get(i).setJiesan(0);
						}
					}
					returnMap.put("userid", userBean.getUserid());
					returnMap.put("type", "dissolve");
					returnMap.put("state", "402");// 房间解散失败
					sendMessageTo(returnMap);
					sendMessageToAll(returnMap, rb);
					returnMap.clear();
				}
			}
		}

		// 退出房间
		if ("exit_room".equals(jsonTo.get("type"))) {
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
				returnMap.put("type", "exitGame");
				returnMap.put("room_branker", rb.getRoom_branker());
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
				returnMap.clear();
				rb.Exit_Room(rb, userBean);
			} else {
				returnMap.put("id", userBean.getUserid());
				returnMap.put("state", "0");// 房间没人 直接解散
				returnMap.put("type", "exitGame");
				sendMessageTo(returnMap);
				sendMessageToAll(returnMap, rb);
				returnMap.clear();
				for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
					Public_State.clients.remove(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
				}
				Public_State.PKMap.remove(String.valueOf(rb.getRoom_number()));
			}

		}

		// 正常退出或者强退
		if ("exit_room2".equals(jsonTo.get("type"))) {
			Integer exit_type = Integer.valueOf(jsonTo.get("exit_type"));
			// 退出
			Exit_Room(exit_type);
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

	private void Exit_Room1(int type) {
		rb.getLock().lock(); // 剔除自己
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUserid() == userBean.getUserid()) {
				rb.getGame_userList(0).remove(i);
				break;
			}
		}
		Map<String, Object> map = new HashMap<String, Object>(); // 清除自己的位置
		rb.remove_options(userBean.getUserid()); // 如果房间没人
		if (rb.getGame_userList(0).size() == 0) {
			Public_State.PKMap.remove(rb.getRoom_number());
			System_Mess.system_Mess.ToMessagePrint("房间清除");
		}
		map.put("type", "exit");
		map.put("id", userBean.getUserid());
		rb.getLock().unlock();
		if (type == 1) {
			sendMessageTo(map);
		}
		sendMessageToAll(map, rb);
		rb = null;
	}

	/**
	 * 发送消息给除去自己以外的所有人
	 * 
	 * @throws IOException
	 */
	public synchronized void sendMessageToAll(Map<String, Object> returnMap, RoomBean rb) {
		// String returnjson = gson.toJson(returnMap).toString(); //
		// RC4.encry_RC4_string(gson.toJson(returnMap).toString());;
		for (UserBean user : rb.getGame_userList(0)) {
			PK_WebSocket webSocket = Public_State.clients.get(user.getUserid() + "");
			if (webSocket != null && webSocket.session.isOpen() && userBean.getGametype() != 2) {
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

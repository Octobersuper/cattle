/**
 * 
 */
package com.zcf.game_util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.util.CardType;

/**
 * @author guolele
 * @date 2019年2月20日 下午3:08:07
 * 
 */
@SuppressWarnings("unused")
public class GameService extends Thread {
	private RoomBean rb;
	private UserBean userBean;

	public GameService() {
		this.rb = new RoomBean();
		this.userBean = new UserBean();
	}

	/**
	 * 创建房间
	 *
	 * @param map
	 * @param userBean
	 * @return @throws
	 */
	public RoomBean Esablish(Map<String, String> map, UserBean userBean) {
		// 创建房间
		RoomBean rb = CreatRoom.EcoSocket(map);
		// 加入自己
		rb.getGame_userList(0).add(userBean);
		// 参与分
		rb.setFen(Integer.parseInt(map.get("fen")));
		// 底分
		rb.setDi_fen(Integer.parseInt(map.get("di_fen")));
		// 最大回合数
		rb.setMax_number(Integer.parseInt(map.get("max_number")));
		// 最大参与人数
		rb.setFoundation(Integer.parseInt(map.get("foundation")));
		// 初始化用户
		userBean.Initialization();
		// 规则
		rb.setRule(Integer.parseInt(map.get("rule")));
		// 牌型
		String brand_type = String.valueOf(map.get("brand_type"));
		rb.setBrand_type(brand_type.split("-"));

		// 往座位添加用户
		rb.setUser_positions(new int[rb.getFoundation()]);
		for (int i = 0; i < rb.getUser_positions().length; i++) {
			rb.getUser_positions()[i] = -1;
		}
		rb.setBranker_ord(0);
		// 游戏类型
		rb.setRoom_type(Integer.parseInt(map.get("room_type")));
		userBean.setGametype(1);
		/******************* 往记录类插入数据 ***************/
		// record.setRoom_type(rb.getRoom_type());
		//record.setFen(rb.getFen());
		//record.setRoom_number(rb.getRoom_number());
		//record.setRoom_id(userBean.getUserid());
		//record.setDatetime(new Date());
		// gd.SaveRoomInfo(record);
		// gd.SaveRoomsInfo(rb);
		// 房间计时器
		rb.setTime_Room(new Time_Room(userBean, rb, this));
		rb.getTime_Room().start();
		return rb;
	}


	/**
	 * 加入房间
	 * 
	 * @throws InterruptedException
	 */
	public RoomBean Matching(Map<String, String> map, UserBean userBean) throws InterruptedException {
		return MatchingRoom.Matching(userBean, String.valueOf(String.valueOf(map.get("room_number"))));
	}

	/**
	 * 修改房间信息
	 *
	 * @param map
	 * @param userBean
	 * 			@param rb @return @throws
	 */
	public RoomBean UpdateRoom(Map<String, String> map, UserBean userBean, RoomBean rb) {
		rb.setFoundation(Integer.parseInt(map.get("foundation")));
		rb.setMinBets(Integer.parseInt(map.get("minBets")));
		rb.setRoom_type(Integer.valueOf(map.get("room_type")));
		return rb;
	}

	/**
	 * 下注 @param money @param userBean @param rb @return @throws
	 */
	public int bets(int money, UserBean userBean, RoomBean rb) {
		rb.getLock().lock();
		// 检测玩家金币
		if (userBean.getMoney() < money) {
			System.out.println("money:::::::::::::::::::::::" + userBean.getMoney() + "------>money:" + money);
			rb.getLock().unlock();
			return 809;
		}
		// 扣除下注
		//gd.UpdateUserMoney(userBean.getUserid(), money, 0);
		userBean.setMoney(userBean.getMoney() - money);
		// 操作状态
		rb.getLock().unlock();
		return 0;
	}

	/**
	 *  观战用户集合  
	 * @throws
	 */
	public List<UserBean> getGuanZhan(RoomBean rb) {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
			if (bean.getUsertype() != 1) {
				list.add(bean);
			}
		}
		return list;
	}
	/**
	 * 根据用户牌型冒泡排序
	 * 
	 * @throws
	 */
	public void users(RoomBean rb, int[] users) {

		/*
		 * for (int i = 0; i < rb.getGame_userList(0).size(); i++) { if
		 * (rb.getGame_userList(0).get(0).getUserid()!=rb.getBranker_id()) {
		 * users[i] = rb.getGame_userList(0).get(i).getUserid(); } }
		 */
		// 冒泡排序
		for (int i = 0; i < users.length; i++) {
			if (users[i] != -1) {
				for (int j = i; j < users.length; j++) {
					if ((j + 1) < users.length && users[j + 1] != -1) {
						UserBean bean = rb.getUserBean(users[i]);
						UserBean bean2 = rb.getUserBean(users[j + 1]);
						if (bean.getUser_brand_type() < bean2.getUser_brand_type()) {
							int maxbrand = users[i];
							users[i] = users[j + 1];
							users[j + 1] = maxbrand;
						}
					}
				}
			}
		}
	}

	/**
	 * 结算 @param id @param rb @return @throws
	 */
	public List<Map<String, Object>> EndGame(RoomBean rb, UserBean userBean) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// 比牌
		int bets = userBean.getBet();
		// 执行比牌
		list = this.pkUser(bets, userBean, rb);
		// 往战绩详情表插入数据
		int winid = (int) list.get(0).get("winid");
		int winmoney = (int) list.get(0).get("money");
		int win_brand_type = (int) list.get(0).get("win_brand_type");

		return list;
	}

	/**
	 * 比牌扣除金币 @param branker_brand @param user_brand @throws
	 */
	public List<Map<String, Object>> pkUser(int bets, UserBean user2, RoomBean rb) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		int money = 0;
		int win = 0;
		int win_brand_type = 0;
		UserBean user1 = rb.getUserBean(rb.getBranker_id());
		// true闲家赢 false庄家赢
		Boolean pkCard = CardType.PkCard(user1.getBrand(), user2.getBrand(), rb);
		if (pkCard && user1.getMoney() > 0) { // 闲家赢
			int odds = CardType.getOdds(user2.getBrand(), rb);
			System.out.println("odds:" + odds);
			System.out.println("getDi_fen:" + rb.getDi_fen());
			System.out.println("getOdd:" + user2.getOdd());
			System.out.println("getOrds:" + rb.getOrds());
			money = rb.getDi_fen() * odds * user2.getOdd() * rb.getOrds();
			if (user1.getMoney() < money) { // 如果庄家不够赔的情况
				//gd.UpdateUserMoney(rb.getBranker_id(), user1.getMoney(), 0);
				user1.setMoney(0);
				//gd.UpdateUserMoney(user2.getUserid(), user1.getMoney(), 1);
				user2.setMoney(user2.getMoney() + user1.getMoney());
				// 设置赢家ID 赢的金额
				user2.setWinnum(user1.getMoney());
				user1.setWinnum(0 - user1.getMoney());
			} else {
				//gd.UpdateUserMoney(rb.getBranker_id(), money, 0);
				user1.setMoney(user1.getMoney() - money);
				//gd.UpdateUserMoney(user2.getUserid(), money, 1);
				user2.setMoney(user2.getMoney() + money + user2.getBet());
				// 设置赢家ID 赢的金额
				user2.setWinnum(money);
				user1.setWinnum(0 - money);
			}
			map.put("winid", user2.getUserid());
			map.put("money", user2.getWinnum());
			map.put("win_brand_type", user2.getUser_brand_type());
			user2.setWinType(1);
			user1.setWinType(0);
		} else {// 庄家赢
			int odds = CardType.getOdds(user1.getBrand(), rb);
			money = rb.getDi_fen() * odds * user2.getOdd() * rb.getOrds();
			if (user2.getMoney() < money) {
				//gd.UpdateUserMoney(user2.getUserid(), user2.getMoney(), 0);
				user2.setMoney(0);
				//gd.UpdateUserMoney(rb.getBranker_id(), user2.getMoney(), 1);
				user1.setMoney(user1.getMoney() + user2.getMoney());
				// 设置赢家ID 赢的金额
				user1.setWinnum(user2.getMoney());
				user2.setWinnum(0 - user2.getMoney());
			} else {
				//gd.UpdateUserMoney(rb.getBranker_id(), money, 1);
				user1.setMoney(user1.getMoney() + money);
				//gd.UpdateUserMoney(user2.getUserid(), money, 0);
				user2.setMoney(user2.getMoney() - money);
				// 设置赢家ID 赢的金额
				user1.setWinnum(money);
				user2.setWinnum(0 - money);
			}
			map.put("winid", user1.getUserid());
			map.put("money", user1.getWinnum());
			map.put("win_brand_type", user1.getUser_brand_type());
		}
		list.add(map);
		return list;
	}

	/**
	 * 開始游戲
	 *
	 * @param roomBean @throws
	 */
	public void Game_Start(RoomBean roomBean, UserBean userBean) {
		roomBean.getLock().lock();
		if (roomBean.getGame_userList().size() >= 2) {
			// Time_Room time_Room = new Time_Room(userBean, roomBean, this, gd,
			// userDao);
			roomBean.setRoom_state(2);
			roomBean.setTimer_user(30);
		}
		roomBean.getLock().unlock();

	}

	/**
	 * 開始游戲
	 *
	 */
	public void Game_Start2(RoomBean roomBean2, UserBean userBean) {
		roomBean2.getLock().lock();
		if (roomBean2.getGame_userList().size() >= 2) {
			roomBean2.setRoom_state(2);
			roomBean2.setTimer_user2(20);
			// roomBean2.setGame_number(roomBean2.getGame_number()+1);//回合数加1
		}
		roomBean2.getLock().unlock();

	}

	/**
	 * 開始游戲
	 *
	 */
	public void Game_Start3(RoomBean roomBean3, UserBean userBean) {
		roomBean3.getLock().lock();
		if (roomBean3.getGame_userList().size() >= 2) {
			roomBean3.setRoom_state(2);
			roomBean3.setTimer_user3(30);
			// roomBean2.setGame_number(roomBean2.getGame_number()+1);//回合数加1
		}
		roomBean3.getLock().unlock();

	}

	/**
	 * 開始游戲
	 *
	 */
	public void Game_Start4(RoomBean roomBean2, UserBean userBean) {
		roomBean2.getLock().lock();
		if (roomBean2.getGame_userList().size() >= 2) {
			roomBean2.setTimer_user4(20);
			// roomBean2.setGame_number(roomBean2.getGame_number()+1);//回合数加1
		}
		roomBean2.getLock().unlock();

	}

	/**
	 * 开牌比点 @param rb @return @throws
	 */
	private int OpenBrand(RoomBean rb) {
		// 查询所有的
		List<UserBean> game_userList = rb.getGame_userList(1);
		rb.getGame_userList();
		return 0;
	}

	/**
	 * 检测相同 @param brands @return @throws
	 */
	private int isBrands(Object[] brands) {
		int brandcount = 0;
		// 判断是否有相同牌型
		for (int i = 0; i < brands.length; i++) {
			int[] brand_user = (int[]) brands[i];
			if ((i + 1) < brands.length) {
				int[] brand_user2 = (int[]) brands[i + 1];
				if (brand_user[1] == brand_user2[1]) {
					brandcount++;
				} else {
					break;
				}
			}
		}
		return brandcount;
	}

	/**
	 * 查询倍率
	 *
	 * @param rb
	 * @return @throws
	 */
	public int odds(int id, RoomBean rb) {
		UserBean user = rb.getUserBean(id);
		int user_brand_type = user.getUser_brand_type();
		return user_brand_type;

	}

	/**
	 * 坐下座位 @param userBean2 @param rb2 @throws
	 */
	public int Sit_down(UserBean userBean, RoomBean rb) {
		for (int i = 0; i < rb.getUser_positions().length; i++) {
			if (rb.getUser_positions()[i] == -1) {
				// 如果座位未满 则按顺序添加用户到座位
				rb.getUser_positions()[i] = userBean.getUserid();
				if (i == 0 && rb.getUser_positions()[i] == -1) {
					// 第一个坐下的人拥有开始游戏的权限
					rb.setRoom_branker(userBean.getUserid());
				}
				userBean.setGametype(1);
				userBean.setUsertype(1);// 设置用户已坐下
				userBean.setStart_money(userBean.getMoney());
				return 0;
			}
		}
		return -1; // 房间已满 坐下失败
	}

	/**
	 * 结算 @param rb2 @param bean1 @param bean2 @throws
	 */
	public void EndGame2(RoomBean rb2, UserBean bean1, UserBean bean2) {
		bean1.setMoney(bean1.getMoney() - rb2.getDi_fen());
		bean1.setWinnum(bean1.getWinnum() - rb2.getDi_fen());
		//gd.UpdateUserMoney(bean1.getUserid(), rb.getDi_fen(), 0);

		bean2.setMoney(bean2.getMoney() + rb2.getDi_fen());
		bean2.setWinnum(bean2.getWinnum() + rb2.getDi_fen());
		//gd.UpdateUserMoney(bean2.getUserid(), rb2.getDi_fen(), 1);
	}

	/**
	 * 判断是否全部做了亮牌操作 @param rb2 @throws
	 */
	public int Open_Brand_Count(RoomBean rb) {
		// TODO Auto-generated method stub
		int count = 0;
		int user_count = 0;
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getOpen_brand() == 1) {
				count++;
			}
			if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
				user_count++;
			}
		}
		if (count == user_count) {
			return 1; // 如果全部亮牌 则返回1
		}
		return 0; // 如果没全部做亮牌操作 则返回0
	}

}

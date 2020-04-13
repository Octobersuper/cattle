/**
 * 
 */
package com.zcf.game_bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zcf.service.impl.UserTableServiceImpl;
import com.zcf.util.CardType;


/**
 * @author guolele
 * @date 2019年2月20日 上午9:20:50 房间类
 */
@SuppressWarnings("unused")
public class RoomBean {
	// 房间类型 0抢庄模式 1癞子模式
	private int room_type;
	// 房间号
	private String room_number;
	// 房间内的用户集合
	private List<UserBean> game_userList;
	// 发牌集合
	private List<UserBean> rb_List;
	// 当前庄家的id
	private int branker_id;
	// 房主id 具备开始游戏的权限
	private int room_branker;
	// 参与分数
	private int fen;
	// 最大人数
	private int foundation;
	// 最大回合数
	private int max_number;
	// 底分
	private Double di_fen;
	// 房间状态 0未开始游戏 1倒计时开始游戏阶段 2已开始游戏
	private int room_state = 0;
	// 当前游戏局数
	private int game_number;
	// 房间锁
	private Lock lock;
	// 房间当前注数
	private int bets = 0;
	// 用户位置 用户位置0-7下标代表1-8的座位，值代表用户id
	private int[] user_positions;
	// 房间局的牌
	private String brands;
	// 房间内用户每回合牌型的集合
	private String[] room_brandsList;
	// 每回合游戏结束的时间
	private Date game_time;
	// 胜利得人
	private int victoryid;
	private int times;
	// 抢庄倍数
	private int branker_ord;
	// 倍率
	private int ords = 1;
	// 游戏状态 0 默认未开始 1下注阶段 2开牌阶段 3结算阶段
	private int game_state;
	//准入分
	private int jion_fen;

	public int getJion_fen() {
		return jion_fen;
	}

	public void setJion_fen(int jion_fen) {
		this.jion_fen = jion_fen;
	}

	public RoomBean() {
		// 房间锁
		this.lock = new ReentrantLock(true);
		// 玩家集合
		this.game_userList = new ArrayList<UserBean>();
		this.rb_List = new ArrayList<UserBean>();
		this.user_positions = new int[foundation];
		for (int i = 0; i < user_positions.length; i++) {
			user_positions[i] = -1;
		}
		// 默认回合数为1
		this.game_number = 1;
		// 初始化房间未开始
		this.room_state = 0;
		// 初始化庄家id
		this.branker_id = -1;
		this.fen = 0;
		// 游戏状态
		this.game_state = 0;
		// 初始化游戏房间信息
		Initialization();

	}

	/**
	 * 获取下一个庄家的Id
	 * 
	 * @return
	 */
	public int getNextBrankerId() {
		int userid = -1;
		int thisindex = -1;
		for (int i = 0; i < user_positions.length; i++) {
			if (user_positions[i] != -1 && getUserBean(user_positions[i]).getGametype() == -1) {
				continue;
			}
			if (user_positions[i] == branker_id) {
				thisindex = i;
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((thisindex + 1) == user_positions.length) {
				thisindex = 0;
			} else {
				thisindex++;
			}
			if (user_positions[thisindex] != -1 && getUserBean(user_positions[thisindex]).getGametype() == -1) {
				continue;
			}
			if (user_positions[thisindex] != -1) {
				userid = user_positions[thisindex];
				break;
			}
		}
		// 为该用户创建一个倒计时线程
		// this.time_Room.setUser(getUserBean(userid));
		branker_id = userid;
		return userid;
	}
	
	//找下一个房间拥有者
	public void getRoom_Branker(RoomBean rb, UserBean userBean) {
		for (int i = 0; i < rb.getUser_positions().length; i++) {
			if (rb.getUser_positions()[i] == userBean.getUserid()) {
				rb.getUser_positions()[i] = -1;
			}
			if (i == 0 && rb.getUser_positions()[i] == -1) {
				for (int j = 0; j < rb.getUser_positions().length; j++) {
					if ((j+1) < rb.getUser_positions().length) {
						rb.getUser_positions()[j] = rb.getUser_positions()[j+1];
					}
				}
			}
		}
		
		for (int i = 0; i < rb.getUser_positions().length; i++) {
			if (rb.getUser_positions()[i] != -1 && rb.getUser_positions()[i] != rb.getRoom_branker() && rb.getRoom_branker() == userBean.getUserid()) {
				rb.setRoom_branker(rb.getUser_positions()[i]);
				break;
			}
		}
		
		
	}

	/**
	 * 退出房间
	 *
	 * @return @throws
	 */

	public void Exit_Room(RoomBean rb, UserBean userBean) {
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUserid() == userBean.getUserid()) {
				rb.getGame_userList(0).remove(i);
			}
		}
		for (int i = 0; i < rb.getUser_positions().length; i++) {
			if (rb.getUser_positions()[i] == userBean.getUserid()) {
				rb.getUser_positions()[i] = -1;
			}
		}
		Public_State.clients.remove(String.valueOf(userBean.getUserid()));
	}

	/**
	 * 返回某一个用户 @param i @return @throws
	 */
	public UserBean getUserBean(int userid) {
		for (UserBean userBean : getGame_userList(0)) {
			if (userBean.getUserid() == userid) {
				return userBean;
			}
		}
		return null;
	}

	public UserBean getUserBean1(int userid) {
		for (UserBean userBean : rb_List) {
			if (userBean.getUserid() == userid) {
				return userBean;
			}
		}
		return null;
	}

	/**
	 * 初始化房间
	 */
	public void Initialization() {
		// 初始化扑克牌
		setBrands(1);
		game_number = 1;
		// 初始化游戏未开始
		setRoom_state(0);
		if (game_userList != null) {
			// 初始化用户
			for (UserBean userBean : game_userList) {
				userBean.Initialization();
			}
		}
		// 启用一个线程来开始游戏
		/*
		 * Start_Game start_Game = new Start_Game(this, userBean);
		 * start_Game.start();
		 */
	}

	/**
	 * 获取自定义的房间信息和自定义的用户信息
	 *
	 * @param table @param returnMap @param usertable @throws
	 */
	public void getRoomBean_Custom(String usertable, Map<String, Object> returnMap, String table) {
		getRoomBean_Custom(table, returnMap);
		returnMap.put("userlist", getGame_userList(usertable));
	}

	/**
	 * 自定义机器人用户列
	 *
	 * @param table
	 * 			@param returnMap @param usertable @throws
	 */
	public void getRoomRobot_Custom(String table, Map<String, Object> returnMap) {
		returnMap.put("userlist", getRobot_userList(table));
	}

	public int[] getUser_Brand(RoomBean rb) {
		int count = 0;
		// 往临时数组内添加参与游戏的用户
		for (int i = 0; i < this.getGame_userList(0).size(); i++) {
			if (this.getGame_userList(0).get(i).getUsertype() == 1) {
				count++;
			}
		}
		int[] user_brand = new int[count];
		for (int i = 0; i < user_brand.length; i++) {
			if (this.getGame_userList(0).get(i).getUsertype() == 1) {
				user_brand[i] = this.getGame_userList(0).get(i).getUserid();
			}
		}

		// 冒泡排序
		for (int i = 0; i < user_brand.length; i++) {
			for (int j = i; j < user_brand.length; j++) {
				if ((j + 1) < user_brand.length) {
					UserBean userBean2 = rb.getUserBean(user_brand[i]);
					UserBean userBean3 = rb.getUserBean(user_brand[j + 1]);
					if (CardType.PkCard(userBean3.getBrand(), userBean2.getBrand(), rb)) {
						int maxbrand = user_brand[i];
						user_brand[i] = user_brand[j + 1];
						user_brand[j + 1] = maxbrand;
					}
				}
			}
		}

		return user_brand;

	}

	/**
	 * 判断牌型大小
	 *
	 * @param bean1
	 * 			@param bean2 @return @throws
	 */
	public boolean maxBrand(UserBean bean1, UserBean bean2) {
		if (bean1.getUser_brand_type() > bean2.getUser_brand_type()) {
			return true;
		} else if (bean1.getUser_brand_type() == bean2.getUser_brand_type()) {
			if (this.getBrand_Color(bean1) < this.getBrand_Color(bean2)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 自定义获取用户列
	 */
	public List<Map<String, Object>> getGame_userList(String usertable) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < getGame_userList(0).size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			getGame_userList(0).get(i).getUser_Custom(usertable, map);
			list.add(map);
		}
		return list;
	}

	/**
	 * 自定义获取机器人列
	 *
	 * @param usertable
	 * 			@return @throws
	 */
	public List<Map<String, Object>> getRobot_userList(String usertable) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < rb_List.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			rb_List.get(i).getRobot_Custom(usertable, map);
			list.add(map);
		}
		return list;
	}

	/**
	 * 自定义获取房间列
	 * 
	 */
	public void getRoomBean_Custom(String usertable, Map<String, Object> returnMap) {
		String[] rooms = usertable.split("-");
		for (String room : rooms) {
			if (room.equals("room_type"))
				returnMap.put(room, room_type);
			if (room.equals("room_number"))
				returnMap.put(room, room_number);
			if (room.equals("game_number"))
				returnMap.put(room, game_number);
			if (room.equals("branker_id"))
				returnMap.put(room, branker_id);
			if (room.equals("fen"))
				returnMap.put(room, fen);
			if (room.equals("di_fen"))
				returnMap.put(room, di_fen);
			if (room.equals("foundation"))
				returnMap.put(room, foundation);
			if (room.equals("bets"))
				returnMap.put(room, bets);
			if (room.equals("rb_List"))
				returnMap.put(room, rb_List);
			if (room.equals("room_branker"))
				returnMap.put(room, room_branker);
			if (room.equals("user_positions"))
				returnMap.put(room, user_positions);
			if (room.equals("brands"))
				returnMap.put(room, brands);
			if (room.equals("room_brandsList"))
				returnMap.put(room, room_brandsList);
			if (room.equals("max_number"))
				returnMap.put(room, max_number);
			if (room.equals("ords"))
				returnMap.put(room, ords);
			if (room.equals("game_state"))
				returnMap.put(room, game_state);
		}
	}


	/**
	 * 初始化扑克牌
	 */
	public void setBrands(int number) {
		StringBuffer brand = new StringBuffer();
		for (int i = 0; i < 52 * number; i++) {
			if (i > 0) {
				brand.append("-");
			}
			brand.append(i);
		}
		this.brands = brand.toString();
	}

	/**
	 * 初始化用户位置
	 * 
	 */
	public void setUser_positions(int[] user_positions) {
		this.user_positions = user_positions;
	}

	/**
	 * 初始化用户操作状态
	 * 
	 * @throws
	 */
	public void Initialization_UserBrand() {
		for (UserBean user : getGame_userList(0)) {
			user.setBrandstatus(0);
		}
	}

	/**
	 * 加入用户位置 按顺序加入 @param userBean @throws
	 */
	public void setUser_positions(UserBean userBean) {
		for (int i = 0; i < user_positions.length; i++) {
			if (user_positions[i] == -1) {
				user_positions[i] = userBean.getUserid();
				break;
			}
		}
	}

	/**
	 * 剔除指定用户的位置
	 * 
	 */
	public void remove_options(int userid) {
		for (int i = 0; i < user_positions.length; i++) {
			if (user_positions[i] == userid) {
				user_positions[i] = -1;
			}
		}
	}

	/**
	 * 删除一名用户 @param userid @throws
	 */
	public void User_Remove(int userid) {
		for (int i = 0; i < game_userList.size(); i++) {
			if (game_userList.get(i).getUserid() == userid) {
				game_userList.remove(i);
				break;
			}
		}

	}

	/**
	 * 扣除底注 @param gameDao @throws
	 */
	public void DeductionBottom_Pan(UserTableServiceImpl gameDao) {
		/*for (UserBean userBean : game_userList) {
			if (userBean.getGametype() != -1) {
				gameDao.UpdateUserMoney(userBean.getUserid(), bets, 0);
				userBean.setMoney(userBean.getMoney() - bets);
			}
		}*/
	}

	/**
	 * 用户下注
	 *
	 * @param userBean @param bets @param betstype @throws
	 */
	public void DeductionBottom_Pan(UserBean userBean, int bets, int betstype) {
		// 减去用户金钱
		userBean.setMoney(userBean.getMoney() - bets);
		// 添加用户输赢总数
		userBean.setWinnum(userBean.getWinnum() + bets);
	}

	/**
	 * 添加用户到用户集合
	 *
	 * @return @throws
	 */
	/*
	 * public List<UserBean> setGame_userList(){ List<UserBean> list = new
	 * ArrayList<UserBean>(); for (UserBean userBean : game_userList) {
	 * //没有弃牌且已参与游戏的用户 if(userBean.getGametype() != -1){ list.add(userBean); } }
	 * return list; }
	 */
	/**
	 * 获取房间内用户集合 type=0 返回实例 1返回过滤 @param type @return @throws
	 */
	public List<UserBean> getGame_userList(int type) {
		// 返回本实例
		if (type == 0) {
			List<UserBean> list = new ArrayList<UserBean>();
			for (UserBean userBean : game_userList) {
				// 获取已经坐下的玩家
				if (userBean.getUsertype()!=0) {
					list.add(userBean);
				}
			}
			return list;
		}else if(type == 1){
			return game_userList;
		}
		List<UserBean> list = new ArrayList<UserBean>();
		for (UserBean userBean : game_userList) {
			// 没有弃牌且已参与游戏的用户
			if (userBean.getBrandstatus() != 2 && userBean.getGametype() != -1) {
				list.add(userBean);
			}
		}
		return list;
	}

	/**
	 * 发牌
	 *
	 * @param number @throws
	 */
	public void GrantBrand(int number, RoomBean rb) {
		String[] brand = brands.split("-");

		// 正常发牌
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
				UserBean bean = getUserBean(rb.getGame_userList(0).get(i).getUserid());
				if (bean == null) {
					continue;
				}
				for (int j = 0; j < number; j++) {
					bean.setBrand(getBrand(), bean);
				}
				// 发完牌就获取牌型
				bean.setUser_brand_type(bean.BrandCount(rb, bean.getBrand()));
				CardType.BrandIndex(bean.getBrand(), bean);
			}
		}
	}

	/**
	 * 判断牌组内是否有作弊牌
	 *
	 * @param brand
	 * @param brand_a
	 * @return @throws
	 */
	public int If_Brand(String[] brand, int brand_a) {
		int count = 0;
		for (int i = 0; i < brand.length; i++) {
			if (Integer.parseInt(brand[i]) == brand_a) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 随机获取一张牌
	 * 
	 */
	public int getBrand() {
		String[] brand = brands.split("-");
		int index = (int) (Math.random() * brand.length);
		// 获取总牌的id
		int brands_index = Integer.parseInt(brand[index]);
		// 剔除此牌
		RemoveBrand(brands_index, brand);
		return brands_index;
	}

	/**
	 * 剔除一张牌 @param brands_index @param brand @throws
	 */
	public void RemoveBrand(int index, String[] brand) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < brand.length; i++) {
			if (Integer.parseInt(brand[i]) != index) {
				if (!"".equals(sb.toString()))
					sb.append("-");
				sb.append(brand[i]);
			}
		}
		this.brands = sb.toString();
	}

	/**
	 * 随机获取一个庄家
	 *
	 * @param rb
	 * @return @throws
	 */
	public void selectBranker_id(RoomBean rb) {
		int count = 0;
		int index;
		List<Integer> user = new ArrayList<Integer>();
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUsertype() == 1
					&& rb.getGame_userList(0).get(i).getRobbery() == rb.getBranker_ord()) {
				count++;
				user.add(rb.getGame_userList(0).get(i).getUserid());
			}
		}

		if (count != 0) {
			index = (int) (Math.random() * count);
			for (int i = 0; i < user.size(); i++) {
				rb.setBranker_id(user.get(index));
			}
		} else { // 如果没人抢庄则随机选取一位
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				if (rb.getGame_userList(0).get(i).getUsertype() == 1
						&& rb.getGame_userList(0).get(i).getGametype() != 2) {
					count++;
					user.add(rb.getGame_userList(0).get(i).getUserid());
				}
			}
			index = (int) (Math.random() * count);
			// for (int i = 0; i < user.size(); i++) {
			rb.setBranker_id(user.get(index));
			// }
		}
		UserBean bean = this.getUserBean(rb.getBranker_id());
		// 添加庄家到发牌集合
		this.rb_List.add(bean);
	}

	/**
	 * 随机获取一个庄家
	 *
	 * @param rb
	 * @return @throws
	 */
	public void selectBranker_id3(RoomBean rb) {
		int count = 0;
		int index;
		List<Integer> user = new ArrayList<Integer>();
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getRobbery() == 1 && rb.getGame_userList(0).get(i).getUsertype() == 1) {
				count++;
				user.add(rb.getGame_userList(0).get(i).getUserid());
			}
		}

		if (count != 0) {
			index = (int) (Math.random() * count);
			for (int i = 0; i < user.size(); i++) {
				rb.setBranker_id(user.get(index));
			}
		} else { // 如果没人抢庄则随机选取一位
			for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
				if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
					count++;
					user.add(rb.getGame_userList(0).get(i).getUserid());
				}
			}
			index = (int) (Math.random() * count);
			// for (int i = 0; i < user.size(); i++) {
			rb.setBranker_id(user.get(index));
			rb.setOrds(1);
			// }
		}
		UserBean bean = this.getUserBean(rb.getBranker_id());
	}

	/**
	 * 开始游戏 @param userBean2 @param rb @throws
	 */
	public int Start_Game(UserBean userBean, RoomBean rb) {
		int count = 0;
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
				count++;
			}
		}
		if (count >= 2 && userBean.getUserid() == rb.getRoom_branker()) {
			rb.setRoom_state(2); // 游戏开始
			return 2;
		}
		return 0;
	}

	/**
	 * 查询两张牌的花色 @param userBean2 @throws
	 */
	public int getBrand_Color(UserBean userBean) {
		int[] brand = userBean.getBrand();
		if (brand[0] != -1 && brand[1] != -1) {
			if (brand[0] / 9 == 1 || brand[0] / 9 == 3 && brand[1] / 9 == 1 || brand[1] / 9 == 3) {
				return 100; // 双黑
			} else if (brand[0] / 9 == 1 || brand[0] / 9 == 3 && brand[1] / 9 == 2 || brand[1] / 9 == 4) {
				return 110; // 红黑
			} else if (brand[0] / 9 == 2 || brand[0] / 9 == 4 && brand[1] / 9 == 1 || brand[1] / 9 == 3) {
				return 110; // 红黑
			} else {
				return 120; // 双红
			}
		}
		return -1;
	}

	/************************ get\set ****************************/

	public Date getGame_time() {
		return game_time;
	}

	public void setBrands(String brands) {
		this.brands = brands;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public void setGame_time(Date game_time) {
		this.game_time = game_time;
	}

	public int getRoom_type() {
		return room_type;
	}

	public void setRoom_type(int room_type) {
		this.room_type = room_type;
	}

	public String getRoom_number() {
		return room_number;
	}

	public void setRoom_number(String room_number) {
		this.room_number = room_number;
	}

	public void setGame_userList(List<UserBean> game_userList) {
		this.game_userList = game_userList;
	}

	public int getBranker_id() {
		return branker_id;
	}

	public void setBranker_id(int branker_id) {
		this.branker_id = branker_id;
	}

	public int getFen() {
		return fen;
	}

	public void setFen(int fen) {
		this.fen = fen;
	}

	public int getRoom_state() {
		return room_state;
	}

	public int getFoundation() {
		return foundation;
	}

	public void setFoundation(int foundation) {
		this.foundation = foundation;
	}

	public void setRoom_state(int room_state) {
		this.room_state = room_state;
	}

	public int getGame_number() {
		return game_number;
	}

	public void setGame_number(int game_number) {
		this.game_number = game_number;
	}

	public int getBets() {
		return bets;
	}

	public void setBets(int bets) {
		this.bets = bets;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int[] getUser_positions() {
		return user_positions;
	}

	public String getBrands() {
		return brands;
	}

	public String[] getRoom_brandsList() {
		return room_brandsList;
	}

	public void setRoom_brandsList(String[] room_brandsList) {
		this.room_brandsList = room_brandsList;
	}

	public List<UserBean> getGame_userList() {
		return game_userList;
	}

	public int getVictoryid() {
		return victoryid;
	}

	public void setVictoryid(int victoryid) {
		this.victoryid = victoryid;
	}

	public Double getDi_fen() {
		return di_fen;
	}

	public void setDi_fen(Double di_fen) {
		this.di_fen = di_fen;
	}

	public int getRoom_branker() {
		return room_branker;
	}

	public void setRoom_branker(int room_branker) {
		this.room_branker = room_branker;
	}

	public List<UserBean> getRb_List() {
		return rb_List;
	}

	public void setRb_List(List<UserBean> rb_List) {
		this.rb_List = rb_List;
	}

	public int getMax_number() {
		return max_number;
	}

	public void setMax_number(int max_number) {
		this.max_number = max_number;
	}

	public int getBranker_ord() {
		return branker_ord;
	}

	public void setBranker_ord(int branker_ord) {
		this.branker_ord = branker_ord;
	}

	public int getOrds() {
		return ords;
	}

	public void setOrds(int ords) {
		this.ords = ords;
	}

	public int getGame_state() {
		return game_state;
	}

	public void setGame_state(int game_state) {
		this.game_state = game_state;
	}

	/**
	 * 解散房间 @param rb @param userBean2 @throws
	 */
	public int getJiesan(RoomBean rb, UserBean userBean2) {
		int count = 0;
		int count_a = 0;
		for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
			if (rb.getGame_userList(0).get(i).getUsertype() == 1 && rb.getGame_userList(0).get(i).getJiesan() == 1) {
				count++;
			}
			if (rb.getGame_userList(0).get(i).getUsertype() == 1) {
				count_a++;
			}
		}
		if (count == count_a) {
			return 1;
		}
		return 0;

	}

}

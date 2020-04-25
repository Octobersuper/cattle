/**
 * 
 */
package com.zcf.game_util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.pojo.UserTable;
import com.zcf.service.impl.UserTableServiceImpl;
import com.zcf.util.CardType;
import org.springframework.web.context.ContextLoader;

/**
 * @author guolele
 * @date 2019年2月20日 下午3:08:07
 * 
 */
@SuppressWarnings("unused")
public class GameService extends Thread {
	private RoomBean rb;
	private UserBean userBean;

	//用户数据处理
	private UserTableServiceImpl us = (UserTableServiceImpl) ContextLoader.getCurrentWebApplicationContext().getBean(
			"userService");

	public GameService() {
		this.rb = new RoomBean();
		this.userBean = new UserBean();
	}

	/**
	 * 创建房间
	 *
	 * @param map
	 * @param
	 * @return @throws
	 */
	public RoomBean Esablish(Map<String, String> map,String roomNumber) {
		// 创建房间
		RoomBean rb = CreatRoom.EcoSocket(roomNumber);
		// 底分
		rb.setDi_fen(Double.valueOf(map.get("di_fen")));
		// 最大回合数
		rb.setMax_number(Integer.parseInt(map.get("max_number")));
		// 最大参与人数
		rb.setFoundation(8);
		rb.setBranker_ord(0);
		// 房间计时器
		//rb.setTime_Room(new Time_Room(userBean, rb, this));
		//rb.getTime_Room().start();
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
	public List<Map<String, Object>> getGuanZhan(RoomBean rb) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < rb.getGame_userList(1).size(); i++) {
			if(i>=6){
				break;
			}
			UserBean bean = rb.getUserBean(rb.getGame_userList(1).get(i).getUserid());
			if (bean.getUsertype() != 1) {
				map = new HashMap<>();
				bean.getUser_Custom("userid-nickname-avatarurl",map);
				list.add(map);
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
	public Map<String, Object> EndGame(RoomBean rb, UserBean userBean) {
		// 比牌
		int bets = userBean.getBet();
		// 执行比牌
		Map<String, Object> map = this.pkUser(bets, userBean, rb);
		return map;
	}

	/**
	 * 比牌扣除金币 @param branker_brand @param user_brand @throws
	 */
	public Map<String, Object> pkUser(int bets, UserBean user2, RoomBean rb) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Double money = 0.0;
		int win = 0;
		int win_brand_type = 0;
		UserBean user1 = rb.getUserBean(rb.getBranker_id());//庄家
		// true闲家赢 false庄家赢
		boolean pkCard = CardType.PkCard(user1.getBrand(), user2.getBrand(), rb);
		if (pkCard) { // 闲家赢
			int odds = user2.getOdds();
			System.out.println("odds:" + odds);
			System.out.println("getDi_fen:" + rb.getDi_fen());
			System.out.println("getOdd:" + user2.getOdd());
			System.out.println("getOrds:" + rb.getOrds());
			money = rb.getDi_fen() * odds * user2.getOdd() * rb.getOrds();
			double waterMoney = money;
			if (user1.getMoney() < money) { // 如果庄家不够赔的情况
				money = user1.getMoney();//获取庄家剩下的钱
				waterMoney = water(money,user2,rb);//抽水  剩下的钱给赢家

				UserTable userTable = new UserTable();
				if(user1.getIsAi()==0){
					userTable.setUserid((long)user1.getUserid());
					userTable.setMoney(0.0);
					userTable.updateById();
				}
				user1.setMoney(0.0);

				user2.setMoney(user2.getMoney() + waterMoney);
				if(user2.getIsAi()==0){
					userTable.setUserid((long)user2.getUserid());
					userTable.setMoney(user2.getMoney());
					userTable.updateById();
				}
				// 设置赢家ID 赢的金额
				user2.setWinnum(user2.getWinnum()+waterMoney);
				user1.setWinnum(user1.getWinnum() + (-money));
			} else {
				waterMoney = water(money,user2,rb);//抽水  剩下的钱进行结算
				user1.setMoney(user1.getMoney() - money);
				UserTable userTable = new UserTable();
				if(user1.getIsAi()==0){
					userTable.setUserid((long)user1.getUserid());
					userTable.setMoney(user1.getMoney());
					userTable.updateById();
				}

				user2.setMoney(user2.getMoney() + waterMoney);
				if(user2.getIsAi()==0){
					userTable.setUserid((long)user2.getUserid());
					userTable.setMoney(user2.getMoney());
					userTable.updateById();
				}
				// 设置赢家ID 赢的金额
				user2.setWinnum(user2.getWinnum()+waterMoney);
				user1.setWinnum(user1.getWinnum() + (-money));
			}
			map.put("winid", user2.getUserid());
			map.put("money", user2.getWinnum());
			map.put("win_brand_type", user2.getUser_brand_type());
			user2.setWinType(1);
			user1.setWinType(0);
		} else {// 庄家赢
			int odds = user1.getOdds();
			money = rb.getDi_fen() * odds * user2.getOdd() * rb.getOrds();
			double waterMoney = money;
			if (user2.getMoney() < money) {
				money = user2.getMoney();//获取闲家剩下的钱
				waterMoney = water(money,user1,rb);//抽水  剩下的钱进行结算
				UserTable userTable = new UserTable();
				if(user2.getIsAi()==0){
					userTable.setUserid((long)user2.getUserid());
					userTable.setMoney(0.0);
					userTable.updateById();
				}
				user2.setMoney(0.0);

				user1.setMoney(user1.getMoney() + waterMoney);
				if(user1.getIsAi()==0){
					userTable.setUserid((long)user1.getUserid());
					userTable.setMoney(user1.getMoney());
					userTable.updateById();
				}
				// 设置赢家ID 赢的金额
				user1.setWinnum(user1.getWinnum()+waterMoney);
				user2.setWinnum(user2.getWinnum()+(-money));
			} else {
				waterMoney = water(money,user1,rb);//抽水  剩下的钱进行结算
				user2.setMoney(user2.getMoney() - money);
				UserTable userTable = new UserTable();
				if(user2.getIsAi()==0){
					userTable.setUserid((long)user2.getUserid());
					userTable.setMoney(user2.getMoney());
					userTable.updateById();
				}

				user1.setMoney(user1.getMoney() + waterMoney);
				if(user1.getIsAi()==0){
					userTable.setUserid((long)user1.getUserid());
					userTable.setMoney(user1.getMoney());
					userTable.updateById();
				}
				// 设置赢家ID 赢的金额
				user1.setWinnum(user1.getWinnum()+waterMoney);
				user2.setWinnum(user2.getWinnum()+(-money));
			}
			map.put("winid", user1.getUserid());
			map.put("money", user1.getWinnum());
			map.put("win_brand_type", user1.getUser_brand_type());
		}
		return map;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:
	 *@ Params:抽水
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/4/22
	 */
	private double water(Double money, UserBean user, RoomBean rb) {
		Double roomWater = rb.getWater();//房间抽水比例
		double m = money * roomWater;//抽水钱数
		System.out.println("抽水总钱数："+m);
		//查询赢的玩家有没有上级 有的话给上级反水
		if(user.getfId()!=0){
			UserTable userTable = new UserTable();
			EntityWrapper<UserTable> ew = new EntityWrapper<>();
			ew.eq("userid",user.getfId());
			UserTable fuji = userTable.selectOne(ew);
			if (fuji != null) {
				double f = (double)fuji.getBackwater()/100;//父级反水比例
				System.out.println("父级反水比例百分比："+fuji.getBackwater());
				System.out.println("父级反水比例："+f);
				double f_money = m * f;
				System.out.println("父级反水钱数："+f_money);
				fuji.setMoney(fuji.getMoney()+f_money);
				fuji.updateById();
			}
		}
		return money-m;
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
		}
		roomBean.getLock().unlock();

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
				userBean.setGametype(1);
				userBean.setUsertype(1);// 设置用户已坐下
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

	public String getCard_type() {
		return us.getCard_type();
	}
}

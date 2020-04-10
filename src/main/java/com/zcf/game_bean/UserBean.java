/**
 * 
 */
package com.zcf.game_bean;

import com.zcf.util.CardType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

/**
 * @author guolele
 * @date 2019年2月20日 上午9:21:05
 * 
 */
@SuppressWarnings("unused")
public class UserBean {
	// 用户id
	private int userid;
	// 用户昵称
	private String nickname;
	// 用户头像
	private String avatarurl;
	// 游戏中的状态0默认1游戏中-1不可游戏 2掉线
	private int gametype = -1;
	// 用戶狀態 0默认 1坐下
	private int usertype = 0;
	// 是否抢庄 0不抢庄 1抢庄
	private int Robbery;
	// 用户手里的牌
	private int[] brand = new int[] { -1, -1, -1, -1, -1 };
	// 充值 0未充值 1已充值
	private int isPay;
	// 下注 （抢庄）
	private int bet;
	// 金币
	private int money;
	private int money_a;
	// 续压注数
	private int onbets;
	// 续压参数
	private String betss;
	// 用户的牌型
	private int user_brand_type;
	// 当前局的输赢分数
	private int winnum;
	// 当前局输赢状态
	private int winType = 0;
	// 胜利者ID
	private int winId;
	// 是否是庄家 默认为0
	private int ISBrand_Id;
	// 操作状态默認0 下注1 开牌2
	private int brandstatus = 0;
	// 是否开牌
	private int open_brand;
	// 手机号
	private String phone;
	// 用户session
	private Session session;
	// 是否继续坐庄
	private int branker_number;
	// 庄家底分
	private int user_fen;
	// 是否开启作弊
	private int type;
	// 作弊牌型
	private String[] Cheat;
	// 作弊牌型id
	private Long fId;
	//闲家倍率
	private int odd = 1;
	//用户游戏之前的金额
	private int start_money;
	//用户输赢总金额
	private int win_money;
	//是否发起解散 0默认 1同意解散
	private int jiesan;
	//
	private String brand_index = "-1";
	
	/**
	 * 
	 * 获取自定义用户详细信息 @param table @param map @throws
	 */
	public void getUser_Custom(String table, Map<String, Object> map) {
		String[] names = table.split("-");
		for (String user : names) {
			if (user.equals("userid"))
				map.put(user, userid);
			if (user.equals("user_fen"))
				map.put(user, user_fen);
			if (user.equals("nickname"))
				map.put(user, nickname);
			if (user.equals("avatarurl"))
				map.put(user, avatarurl);
			if (user.equals("gametype"))
				map.put(user, gametype);
			if (user.equals("brand"))
				map.put(user, brand);
			if (user.equals("isPay"))
				map.put(user, isPay);
			if (user.equals("money"))
				map.put(user, money);
			if (user.equals("bet"))
				map.put(user, bet);
			if (user.equals("winnum"))
				map.put(user, winnum);
			if (user.equals("user_brand_type"))
				map.put(user, user_brand_type);
			if (user.equals("branker_number"))
				map.put(user, branker_number);
			if (user.equals("type"))
				map.put(user, type);
			if (user.equals("Cheat"))
				map.put(user, Cheat);
			if (user.equals("fId"))
				map.put(user, fId);
			if (user.equals("brand_index"))
				map.put(user, brand_index);
			if (user.equals("odd"))
				map.put(user, odd);
			if (user.equals("start_money"))
				map.put(user, start_money);
			if (user.equals("win_money"))
				map.put(user, win_money);
		}
	}
	/**
	 * 自定义获取机器人列
	 *
	 * @param table
	 * @param map    
	 * @throws
	 */
	public void getRobot_Custom(String table, Map<String, Object> map) {
		String[] names = table.split("-");
		for (String user : names) {
			if (user.equals("userid"))
				map.put(user, userid);
			if (user.equals("brand"))
				map.put(user, brand);
			if (user.equals("user_brand_type"))
				map.put(user, user_brand_type);
		}
	}

	/**
	 * 
	 * 初始化用户 @throws
	 */
	public void Initialization() {
		// 初始化手中的牌
		this.brand = new int[] { -1, -1, -1, -1, -1 };
		this.brand_index = "-1";
		// 初始化用户状态
		this.gametype = 0;
		// 抢庄状态默认不抢 1抢庄
		//this.Robbery = 0;
		// 用户默认观战
		this.usertype = 0;
		this.brandstatus = 0;
		// 下注
		this.bet = 0;
		// 当局的输赢分数
		this.winnum = 0;
		// 初始化用户的牌型
		this.user_brand_type = -1;
		this.open_brand = -1;
		// 初始化胜利者信息
		this.winType = 0;
		this.winId = 0;
		// 底分
		this.user_fen = 0;
		// 初始化作弊牌型
		this.Cheat = null;
		this.Robbery = 0;
		//初始化倍率
		this.odd = 1;
	}

	/**
	 * 
	 * 放入一张牌到用户手中 @param brands @throws
	 */
	public void setBrand(int brands,UserBean userBean) {
		int count = 0;
		for (int i : userBean.brand) {
			if (i != -1) {
				count++;
			}
		}
		if (count < userBean.brand.length) {
			userBean.brand[count] = brands;
		}
	}

	/**
	 * 返回 用户手里的最后一张牌的牌值或者下标 type=0返回下标 =1返回牌值
	 *
	 * @param type
	 * @return @throws
	 */
	public int getBrand_M(int type) {
		int count = -1;
		for (int i : this.brand) {
			if (i != -1) {
				count++;
			}
		}
		if (type == 0) {
			// 一副牌的索引
			return this.brand[count];
		} else {
			// 牌值
			int brand_m = this.brand[count] % 13 + 1;
			return brand_m;
		}
	}

	/**
	 * 
	 * 返回用户手里的牌型 @param number @return @throws
	 */
	public int BrandCount(RoomBean rb, int[] brand) {
		//return CardType.getCardType(brand, rb);// 获取牌型
		return CardType.backPatterns(brand, rb);
	}

	public boolean ISMoney(int money) {
		return this.money >= money;
	}

	/********************* get\set ******************************/

	public int getBrandstatus() {
		return brandstatus;
	}

	public int getISBrand_Id() {
		return ISBrand_Id;
	}

	public void setISBrand_Id(int iSBrand_Id) {
		ISBrand_Id = iSBrand_Id;
	}
	public void setBrandstatus(int brandstatus) {
		this.brandstatus = brandstatus;
	}

	public int getWinnum() {
		return winnum;
	}

	public void setWinnum(int winnum) {
		this.winnum = winnum;
	}

	public int getUser_brand_type() {
		return user_brand_type;
	}

	public void setUser_brand_type(int user_brand_type) {
		this.user_brand_type = user_brand_type;
	}

	public int getGametype() {
		return gametype;
	}

	public void setGametype(int gametype) {
		this.gametype = gametype;
	}

	public int getIsPay() {
		return isPay;
	}

	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarurl() {
		return avatarurl;
	}

	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}

	public int[] getBrand() {
		return brand;
	}

	public void setBrand(int[] brand) {
		this.brand = brand;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	public int getOnbets() {
		return onbets;
	}

	public void setOnbets(int onbets) {
		this.onbets = onbets;
	}

	public int getOpen_brand() {
		return open_brand;
	}

	public void setOpen_brand(int open_brand) {
		this.open_brand = open_brand;
	}

	public int getUsertype() {
		return usertype;
	}

	public String getPhone() {
		return phone;
	}

	public String getBetss() {
		return betss;
	}

	public void setBetss(String betss) {
		this.betss = betss;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public int getWinType() {
		return winType;
	}

	public void setWinType(int winType) {
		this.winType = winType;
	}

	public int getWinId() {
		return winId;
	}

	public void setWinId(int winId) {
		this.winId = winId;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getBranker_number() {
		return branker_number;
	}

	public void setBranker_number(int branker_number) {
		this.branker_number = branker_number;
	}

	public int getUser_fen() {
		return user_fen;
	}

	public void setUser_fen(int user_fen) {
		this.user_fen = user_fen;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getCheat() {
		return Cheat;
	}

	public void setCheat(String[] cheat) {
		Cheat = cheat;
	}

	public Long getfId() {
		return fId;
	}

	public void setfId(Long fId) {
		this.fId = fId;
	}

	public int getRobbery() {
		return Robbery;
	}

	public void setRobbery(int robbery) {
		Robbery = robbery;
	}

	public int getMoney_a() {
		return money_a;
	}

	public void setMoney_a(int money_a) {
		this.money_a = money_a;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}
	public String getBrand_index() {
		return brand_index;
	}
	public void setBrand_index(String brand_index) {
		this.brand_index = brand_index;
	}
	public int getOdd() {
		return odd;
	}
	public void setOdd(int odd) {
		this.odd = odd;
	}
	public int getStart_money() {
		return start_money;
	}
	public void setStart_money(int start_money) {
		this.start_money = start_money;
	}
	public int getWin_money() {
		return win_money;
	}
	public void setWin_money(int win_money) {
		this.win_money = win_money;
	}
	public int getJiesan() {
		return jiesan;
	}
	public void setJiesan(int jiesan) {
		this.jiesan = jiesan;
	}
	
}

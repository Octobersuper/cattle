package com.zcf.game_bean;

import com.zcf.game_center.PK_WebSocket;
import com.zcf.util.UtilClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Public_State {
	// 客户端的线程池(房卡)
	public static Map<String, PK_WebSocket> clients = new ConcurrentHashMap<String, PK_WebSocket>();
	// 客户端的房间
	public static Map<String, RoomBean> PKMap = new LinkedHashMap<String, RoomBean>();
	// 是否开启记录
	public static boolean record_bool = false;
	// 初始钻石
	public static int diamond;
	static {
		// CheckOut.checkOut();
		diamond = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "diamond"));
	}

	/**
	 * 检测是否在房间中
	 * 
	 * @param userid
	 * @return
	 */
	public static String ISUser_Room(int userid) {
		//麻将
		for (String key : PKMap.keySet()) {
			RoomBean roomBean = PKMap.get(key);
			for (UserBean userBean : roomBean.getGame_userList()) {
				if (userBean.getUserid() == userid)
					return roomBean.getRoom_number();
			}
		}
		return null;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:
	 *@ Params:获取一个能发送消息的线程
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/4/16
	 */
	public static PK_WebSocket getPkWebSocket() {
		for (String key : clients.keySet()) {
			PK_WebSocket socket = clients.get(key);
			if(socket.session != null && socket.session.isOpen()){
				return socket;
			}
		}
		return null;
	}
}

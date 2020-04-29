/**
 * 
 */
package com.zcf.game_util;

import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;

/**
 * @author guolele
 * @date 2019年2月21日 下午1:52:39
 * 
 */
public class MatchingRoom {

	
	/**
	 * @throws InterruptedException
	 * 加入房间
	 * @param userBean
	 * @param
	 * @return    
	 * @throws
	 */
	public static RoomBean Matching(UserBean userBean, String room_number) {
		
		RoomBean rb = Public_State.PKMap.get(room_number);
		rb.getLock().lock();
		//当前房间未满
		rb.getGame_userList(1).add(userBean);
		rb.getLock().unlock();
		return rb;
	}

}

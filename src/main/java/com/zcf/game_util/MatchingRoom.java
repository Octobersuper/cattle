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
	 * @param string
	 * @return    
	 * @throws
	 */
	public static RoomBean Matching(UserBean userBean, String room_number) {
		
		RoomBean rb = Public_State.PKMap.get(room_number);
		rb.getLock().lock();
		//当前房间未满
		if (rb.getGame_userList(0).size()<rb.getFoundation()) {
			rb.getGame_userList(0).add(userBean);
			if (rb.getGame_number() == 1 && rb.getGame_userList(0).size() == 1) {
				rb.setBranker_id(userBean.getUserid());
			}
			//添加自己的座位
			rb.getLock().unlock();
			return rb;
			
		}
		rb.getLock().unlock();
		return null;
	}

}

/**
 * 
 */
package com.zcf.game_util;

import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.util.System_Mess;

import java.util.List;


/**
 * @author guolele
 * @date 2019年3月18日 下午8:13:41
 * 
 */
public class NewStartGame extends Thread{
	UserBean userBean;
	List<UserBean> userList;
	RoomBean rb;
	int times = 15;
	
	public NewStartGame(RoomBean rb, UserBean userBean){
		this.rb = rb;
		this.userList = rb.getGame_userList();
		this.userBean = userBean;
	}
	
	@Override
	public void run(){
		while (times>0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			times--;
			rb.setTimes(times);
			System_Mess.system_Mess.ToMessagePrint("倒计时sss----------->" + times);
		}
	}
	

}

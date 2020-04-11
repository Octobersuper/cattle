/**
 *
 */
package com.zcf.game_util;

import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 创建房间
 *
 * @author guolele
 * @date 2019年2月20日 下午3:33:08
 *
 */
public class CreatRoom {

    public static RoomBean EcoSocket(String room_number) {
        if (Public_State.PKMap.get(room_number) == null) {
            RoomBean rb = new RoomBean();
            // 放入房间号
            rb.setRoom_number(room_number);
            // 把房间实例放入房间map
            Public_State.PKMap.put(room_number, rb);
            return rb;
        }
        return null;
    }
}

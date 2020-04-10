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

    public static synchronized RoomBean EcoSocket(Map<String, String> map) {
        String room_number = "";
        while (true) {
            for (int i = 0; i < 5; i++) {
                room_number += (int) (Math.random() * 10);
            }
            System.out.println("0" + room_number);
            if (Public_State.PKMap.get("0" + room_number) == null) {
                RoomBean rb = new RoomBean();
                // 放入房间号
                rb.setRoom_number("0" + room_number);
                // 把房间实例放入房间map
                Public_State.PKMap.put("0" + room_number, rb);
                return rb;
            }
        }
    }

    /**
     *
     * @param userBean @param gameService @return @throws
     */
    public static RoomBean EcoSocket3(UserBean userBean, int di_fen, int fen, int type, String brand_type,
                                      GameService gameService) {
        String room_number = "";
        while (true) {
            for (int i = 0; i < 6; i++) {
                room_number += (int) (Math.random() * 10);
            }
            System.out.println("房间号：" + room_number);
            if (Public_State.PKMap.get(room_number) == null) {
                // 初始化用户
                userBean.Initialization();
                RoomBean rb = new RoomBean();
                rb.getGame_userList().add(userBean);
/*				rb.getGame_userList().add(rb.Robot_a());// 添加庄家
				rb.getGame_userList().add(rb.Robot_b());// 添加闲家
				rb.getGame_userList().add(rb.Robot_c());// 添加荷官
*/                // 初始化用户位置
                rb.setUser_positions(new int[]{userBean.getUserid(), -1, -1, -1, -1, -1, -1, -1, -1, -1});
                //rb.setUser_positions(rb.Robot_c());
                // 准备锁
                rb.setLock(new ReentrantLock(true));
                // 放入房间号
                rb.setRoom_number(room_number);
                // 放入底分
                rb.setDi_fen(di_fen);
                // 放入参与分
                rb.setFen(fen);
                // 游戏类型
                rb.setRoom_type(type);
                //特殊牌型
                rb.setBrand_type(brand_type.split("-"));
                // 把房间实例放入房间map
                Public_State.PKMap.put(room_number, rb);
                // 房间计时器
                rb.setTime_Room(new Time_Room(userBean, rb, gameService));
                rb.getTime_Room().start();
                return rb;
            }
        }
    }

}

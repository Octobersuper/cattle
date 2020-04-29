package com.zcf.controller;


import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.UserBean;
import com.zcf.game_center.PK_WebSocket;
import com.zcf.pojo.UserTable;
import com.zcf.service.impl.UserTableServiceImpl;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import com.zcf.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
@CrossOrigin
@RestController
@RequestMapping("/userTable")
public class UserTableController {


    @Autowired
    UserTableServiceImpl userService;

    /**
     *@Author:ZhaoQi
     *@methodName:前台登录
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("login")
    public Body login(UserTable user){
        return userService.login(user);
    }

    /**
     *@Author:ZhaoQi
     *@methodName:获取某用户的个人信息
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/26
     */
    @GetMapping("getUser")
    public Body getUser(UserTable user){
        return userService.getUser(user);
    }

    /**
     *@Author:ZhaoQi
     *@methodName:查询所有用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @GetMapping("getUsers")
    public LayuiJson getUsers(UserTable user, @RequestParam(value = "pageNum") int pageNum,
                              @RequestParam(value = "pageSize") int pageSize){
        return userService.getUsers(user,pageNum,pageSize);
    }

    /**
     *@Author:ZhaoQi
     *@methodName:查询所有用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @GetMapping("selectUser")
    public Body getUsers(UserTable user){
        return Body.newInstance(user.selectAll());
    }

    /**
     *@Author:ZhaoQi
     *@methodName:修改某个用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("update")
    public Body update(UserTable user){
        if(user.getWinodds()!=null){
            PK_WebSocket socket = Public_State.clients.get(String.valueOf(user.getUserid()));
            if (socket != null) {
                socket.userBean.setWinodds(user.getWinodds());
            }
        }
        if(user.getfId()!=null){
            UserBean userBean = userService.selectByid(user.getfId());
            if(userBean.getRole()!=1){
                return Body.newInstance(451,"非代理玩家无法绑定");
            }
        }
        boolean b = user.updateById();
        if (b){
            HashMap<String, Object> map = new HashMap<>();
            if(user.getfId()!=null){
                user = user.selectById();
                map.put("type",0);
                map.put("fId",user.getfId());
                return Body.newInstance(map);
            }else if(user.getZfb()!=null || user.getBankcard()!=null){
                user = user.selectById();
                map.put("type",1);
                map.put("zfb",user.getZfb());
                map.put("bankcard",user.getBankcard());
                return Body.newInstance(map);
            }else
                return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@Author:ZhaoQi
     *@methodName:删除某个用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("delete")
    public Body delete(UserTable user){
        return Body.newInstance(userService.deleteById(user.getUserid()));
    }
}


package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.GameBacktableMapper;
import com.zcf.pojo.GameBacktable;
import com.zcf.util.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/gameBacktable")
public class GameBacktableController {

    @Autowired
    GameBacktableMapper bm;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("login")
    public Body login(GameBacktable backtable){
        EntityWrapper<GameBacktable> ew = new EntityWrapper<>();
        ew.eq("account",backtable.getAccount()).eq("password",backtable.getPassword());
        GameBacktable gameBacktable = backtable.selectOne(ew);
        if (gameBacktable != null) {
            return Body.newInstance(gameBacktable);
        }
        return Body.newInstance(451,"账号或密码错误");
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:修改
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("update")
    public Body update(GameBacktable backtable){
        boolean b = backtable.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}


package com.zcf.controller;


import com.zcf.pojo.GameIntroduce;
import com.zcf.service.impl.GameIntroduceServiceImpl;
import com.zcf.util.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
@RequestMapping("/gameIntroduce")
public class GameIntroduceController {

    @Autowired
    GameIntroduceServiceImpl gs;

    @GetMapping("get")
    public Body get(){
        GameIntroduce gameIntroduce = gs.selectById(1);
        return Body.newInstance(gameIntroduce);
    }

    @PostMapping("update")
    public Body update(GameIntroduce gameIntroduce){
        boolean b = gameIntroduce.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}


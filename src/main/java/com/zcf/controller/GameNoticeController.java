package com.zcf.controller;


import com.zcf.mapper.GameNoticeMapper;
import com.zcf.pojo.GameIntroduce;
import com.zcf.pojo.GameNotice;
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
@RequestMapping("/gameNotice")
public class GameNoticeController {

    @Autowired
    GameNoticeMapper gm;

    @GetMapping("get")
    public Body get(){
        GameNotice gameNotice = gm.selectById(1);
        return Body.newInstance(gameNotice);
    }

    @PostMapping("update")
    public Body update(GameNotice gameNotice){
        boolean b = gameNotice.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}


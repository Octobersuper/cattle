package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.GameDiamondShopMapper;
import com.zcf.pojo.DiamondShop;
import com.zcf.pojo.GameDiamondShop;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

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
@RequestMapping("/gameDiamondShop")
public class GameDiamondShopController {


    @Autowired
    GameDiamondShopMapper gds;

    /**
     *@ Author:ZhaoQi
     *@ methodName:修改
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("update")
    public Body update(GameDiamondShop diamondShop){
        boolean b = diamondShop.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取商品充值详情
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("getList")
    public LayuiJson getList(GameDiamondShop diamondShop){
        EntityWrapper<GameDiamondShop> ew = new EntityWrapper<>();
        ew.eq("d_id",diamondShop.getdId());
        List<GameDiamondShop> list = gds.selectList(ew);
        LayuiJson layuiJson = new LayuiJson();
        if (list.size()!=0) {
            layuiJson.setCode(0);
            layuiJson.setCount(list.size());
            layuiJson.setMsg("ok");
            layuiJson.setData(list);
        }else{
            layuiJson.setCode(1);
            layuiJson.setCount(list.size());
            layuiJson.setMsg("no");
            layuiJson.setData(null);
        }
        return layuiJson;
    }
}


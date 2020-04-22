package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.DiamondShopMapper;
import com.zcf.mapper.GameDiamondShopMapper;
import com.zcf.pojo.DiamondShop;
import com.zcf.pojo.GameDiamondShop;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RequestMapping("/diamondShop")
public class DiamondShopController {

    @Autowired
    DiamondShopMapper dsm;
    @Autowired
    GameDiamondShopMapper gdsm;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取商品
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("getList")
    public LayuiJson getList(){
        List<DiamondShop> list = dsm.selectList(null);
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

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取商品信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("getDiamondShop")
    public Body getDiamondShop(DiamondShop diamondShop){
        EntityWrapper<DiamondShop> e = new EntityWrapper<>();
        e.eq("state",1);
        List<DiamondShop> list = dsm.selectList(e);
        if (list.size()!=0) {
            for (DiamondShop d:
                 list) {
                EntityWrapper<GameDiamondShop> ew = new EntityWrapper<>();
                ew.eq("d_id",d.getId());
                d.setList(gdsm.selectList(ew));
            }
            HashMap<Object, Object> map = new HashMap<>();
            map.put("list", list);
            return Body.newInstance(map);
        }
        return Body.newInstance(451,"无数据");
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
    public Body update(DiamondShop diamondShop){
        boolean b = diamondShop.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

}


package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.GameRechargeMapper;
import com.zcf.mapper.UserTableMapper;
import com.zcf.pojo.GameRecharge;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Date;
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
@RequestMapping("/gameRecharge")
public class GameRechargeController {

    @Autowired
    GameRechargeMapper gm;
    @Autowired
    UserTableMapper um;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取充值提现记录
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("get")
    public Body get(GameRecharge gameRecharge){
        EntityWrapper<GameRecharge> ew = new EntityWrapper<>();
        ew.eq("userid",gameRecharge.getUserid()).eq("type",gameRecharge.getType()).orderBy("createtime",false);
        List<GameRecharge> list = gm.selectList(ew);
        if (list.size()!=0) {
            return Body.newInstance(list);
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:同意或者拒绝
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("update")
    public Body update(GameRecharge gameRecharge){
        UserTable userTable = um.selectById(gameRecharge.getUserid());
        if(gameRecharge.getType()==2 && userTable.getMoney()-gameRecharge.getMoney()<0){
            return Body.newInstance(451,"提现金额不能大于本金");
        }else{
            boolean b = gameRecharge.updateById();
            if (b){
                if(gameRecharge.getState()==1){//同意
                    if(gameRecharge.getType()==1){
                        userTable.setMoney(userTable.getMoney()+gameRecharge.getMoney());
                        userTable.updateById();
                    }
                }else{//拒绝
                    if(gameRecharge.getType()==2){
                        userTable.setMoney(userTable.getMoney()+gameRecharge.getMoney());
                        userTable.updateById();
                    }
                }
                return Body.BODY_200;
            }
            return Body.BODY_451;
        }
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:充值或者提现
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("insert")
    public Body inser(GameRecharge gameRecharge){
        UserTable userTable = um.selectById(gameRecharge.getUserid());
        if(gameRecharge.getType()==2 && userTable.getMoney()-gameRecharge.getMoney()<0){
            return Body.newInstance(451,"提现金额不能大于本金");
        }else{
            gameRecharge.setDate(new Date());
            boolean b = gameRecharge.insert();
            if (b){
                if(gameRecharge.getType()==2){
                    userTable.setMoney(userTable.getMoney()-gameRecharge.getMoney());
                    userTable.updateById();
                }
                return Body.BODY_200;
            }
            return Body.BODY_451;
        }
    }
}


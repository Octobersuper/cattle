package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.mapper.GameRechargeMapper;
import com.zcf.mapper.UserTableMapper;
import com.zcf.pojo.GameRecharge;
import com.zcf.pojo.Rooms;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
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
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:后台获取充值提现记录
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @GetMapping("getc")
    public LayuiJson getUsers(GameRecharge gameRecharge, @RequestParam(value = "pageNum") int pageNum,
                              @RequestParam(value = "pageSize") int pageSize) {
        LayuiJson lj = new LayuiJson();
        EntityWrapper<GameRecharge> ew = new EntityWrapper<>();
        ew.orderBy("createtime", false);
        List<GameRecharge> list = gm.selectPage(new Page<Rooms>(pageNum, pageSize), ew);
        Integer count = gm.selectCount(ew);
        if (list != null) {
            for (GameRecharge ga :
                    list) {
                UserTable userTable = um.selectById(ga.getUserid());
                ga.setUser(userTable);
            }
            lj.setCode(0);
            lj.setCount(count);
            lj.setData(list);
            lj.setMsg("成功");
            return lj;
        }
        lj.setCode(1);
        lj.setCount(count);
        lj.setData(null);
        lj.setMsg("暂无数据");
        return lj;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:获取充值提现记录
     * @ Description:
     * @ Return:
     * @ Date:2020/4/8
     */
    @GetMapping("get")
    public Body get(GameRecharge gameRecharge) {
        EntityWrapper<GameRecharge> ew = new EntityWrapper<>();
        if(gameRecharge.getType()==1){
            ew.eq("userid", gameRecharge.getUserid()).eq("type",1).orderBy("createtime", false);
        }else{
            ew.eq("userid", gameRecharge.getUserid()).andNew().eq("type",2).or().eq("type",3).or().eq("type",4).orderBy("createtime", false);
        }
        List<GameRecharge> list = gm.selectList(ew);
        if (list.size() != 0) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("list", list);
            return Body.newInstance(map);
        }
        return Body.newInstance(451,"无数据");
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:同意或者拒绝
     * @ Description:
     * @ Return:
     * @ Date:2020/4/8
     */
    @PostMapping("update")
    public Body update(GameRecharge gameRecharge) {
        UserTable userTable = um.selectById(gameRecharge.getUserid());
        if (userTable == null) {
            return Body.newInstance(451, "用户不存在");
        }
        boolean b = gameRecharge.updateById();
        if (b) {
            if (gameRecharge.getState() == 1) {//同意
                if (gameRecharge.getType() == 1) {
                    userTable.setMoney(userTable.getMoney() + gameRecharge.getMoney());
                    userTable.updateById();
                }
            } else {//拒绝
                if (gameRecharge.getType() == 3 || gameRecharge.getType() ==4) {
                    userTable.setMoney(userTable.getMoney() + gameRecharge.getMoney());
                    userTable.updateById();
                }
            }
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:删除
     * @ Description:
     * @ Return:
     * @ Date:2020/4/10
     */
    @PostMapping("delete")
    public Body delete(GameRecharge gameRecharge) {
        boolean b = gameRecharge.deleteById();
        if (b) {
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:充值或者提现
     * @ Description:
     * @ Return:
     * @ Date:2020/4/8
     */
    @PostMapping("insert")
    public Body inser(GameRecharge gameRecharge) {
        UserTable userTable = um.selectById(gameRecharge.getUserid());
        if (gameRecharge.getType() == 2 && userTable.getMoney() - gameRecharge.getMoney() < 0) {
            return Body.newInstance(451, "提现金额不能大于本金");
        } else {
            gameRecharge.setCreatetime(new Date());
            boolean b = gameRecharge.insert();
            if (b) {
                if (gameRecharge.getType() == 2) {
                    userTable.setMoney(userTable.getMoney() - gameRecharge.getMoney());
                    userTable.updateById();
                }
                HashMap<String, Double> map = new HashMap<>();
                map.put("money",userTable.getMoney());
                return Body.newInstance(map);
            }
            return Body.BODY_451;
        }
    }
}


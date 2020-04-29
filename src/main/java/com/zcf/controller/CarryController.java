package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.CarryMapper;
import com.zcf.mapper.UserTableMapper;
import com.zcf.pojo.Carry;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
@RequestMapping("/carry")
public class CarryController {

    @Autowired
    CarryMapper cm;
    @Autowired
    UserTableMapper um;


    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:转账记录
     * @ Description:
     * @ Return:
     * @ Date:2020/4/8
     */
    @GetMapping("getCarry")
    public Body getCarry(Carry carry) {
        EntityWrapper<Carry> ew = new EntityWrapper<>();
        ew.eq("userid", carry.getUserid()).orderBy("createtime", false);
        List<Carry> list = carry.selectList(ew);
        if (list.size() == 0) {
            return Body.newInstance(451, "没有转账记录");
        }
        HashMap<Object, Object> map = new HashMap<>();
        map.put("list", list);
        return Body.newInstance(map);
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:转账
     * @ Description:
     * @ Return:
     * @ Date:2020/4/8
     */
    @PostMapping("carry")
    public Body carry(Carry carry) {
        UserTable user = um.selectById(carry.getUserid());
        if (user.getMoney() - carry.getMoney() < 0) {
            return Body.newInstance(451, "转入金额不能大于本金");
        } else {
            carry.setCreatetime(new Date());
            boolean insert = carry.insert();
            if (insert) {
                user.setMoney(user.getMoney() - carry.getMoney());
                user.updateById();
                UserTable touser = um.selectById(carry.getTouser());
                touser.setMoney(touser.getMoney() + carry.getMoney());
                touser.updateById();
                HashMap<String, Double> map = new HashMap<>();
                map.put("money",user.getMoney());
                return Body.newInstance(map);
            }
            return Body.BODY_451;
        }
    }
}


package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mapper.InsureRecordMapper;
import com.zcf.mapper.UserTableMapper;
import com.zcf.pojo.InsureRecord;
import com.zcf.pojo.PkRecordTable;
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
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
@CrossOrigin
@RestController
@RequestMapping("/insureRecord")
public class InsureRecordController {

    @Autowired
    InsureRecordMapper im;
    @Autowired
    UserTableMapper um;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取转入转出记录
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("get")
    public Body get(InsureRecord pt){
        EntityWrapper<InsureRecord> e = new EntityWrapper<>();
        e.eq("userid",pt.getUserid()).orderBy("createtime",false);
        List<InsureRecord> list = im.selectList(e);
        if(list.size()!=0){
            HashMap<Object, Object> map = new HashMap<>();
            map.put("list", list);
            return Body.newInstance(map);
        }
        return Body.newInstance(451,"无转存记录");
    }


    /**
     *@ Author:ZhaoQi
     *@ methodName:转入或者转出
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @PostMapping("intoOrOut")
    public Body intoOrOut(InsureRecord insureRecord){
        UserTable user = um.selectById(insureRecord.getUserid());
        insureRecord.setCreatetime(new Date());
        if(insureRecord.getType()==1){//转出
            if(user.getMoney()-insureRecord.getMoney()<0){
                return Body.newInstance(451,"转出金额不能大于本金");
            }else{
                boolean b = insureRecord.insert();
                if(b){
                    user.setMoney(user.getMoney()-insureRecord.getMoney());
                    user.setInsure(user.getInsure()+insureRecord.getMoney());
                    user.updateById();
                    return Body.BODY_200;
                }
                return Body.newInstance(451,"插入记录失败，请稍后重试");
            }
        }else{//转入
            if(user.getInsure()-insureRecord.getMoney()<0){
                return Body.newInstance(451,"转入金额不能大于保险箱金额");
            }else{
                boolean b = insureRecord.insert();
                if(b){
                    user.setMoney(user.getMoney()+insureRecord.getMoney());
                    user.setInsure(user.getInsure()-insureRecord.getMoney());
                    user.updateById();
                    return Body.BODY_200;
                }
                return Body.newInstance(451,"插入记录失败，请稍后重试");
            }
        }
    }
}


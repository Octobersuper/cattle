package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.mapper.PkRecordTableMapper;
import com.zcf.pojo.PkRecordTable;
import com.zcf.util.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
@RequestMapping("/pkRecordTable")
public class PkRecordTableController {

    @Autowired
    PkRecordTableMapper pm;

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:获取战绩记录
     *@ Description:
     *@ Return:
     *@ Date:2020/4/8
     */
    @GetMapping("get")
    public Body get(PkRecordTable pt){
        EntityWrapper<PkRecordTable> e = new EntityWrapper<>();
        e.eq("userid",pt.getUserid()).orderBy("createdate",false);
        List<PkRecordTable> list = pm.selectPage(new Page<PkRecordTable>(0, 50), e);
        if(list.size()!=0){
            HashMap<Object, Object> map = new HashMap<>();
            map.put("list", list);
            return Body.newInstance(map);
        }
        return Body.newInstance(451,"无战绩记录~");
    }

}


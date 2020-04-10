package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_util.CreatRoom;
import com.zcf.mapper.RoomsMapper;
import com.zcf.pojo.Rooms;
import com.zcf.pojo.UserTable;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-09
 */
@CrossOrigin
@RestController
@RequestMapping("/rooms")
public class RoomsController {

    @Autowired
    RoomsMapper rm;

    /**
     *@ Author:ZhaoQi
     *@ methodName:获取房间列表
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @GetMapping("get")
    public LayuiJson get(Rooms rooms, @RequestParam(value = "pageNum") int pageNum,
                         @RequestParam(value = "pageSize") int pageSize){
        LayuiJson lj = new LayuiJson();
        EntityWrapper<Rooms> ew = new EntityWrapper<>();
        ew.orderBy("createtime",false);
        List<Rooms> list = rm.selectPage(new Page<Rooms>(pageNum, pageSize), ew);
        Integer count = rm.selectCount(ew);
        if (list != null) {
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
     *@ Author:ZhaoQi
     *@ methodName:修改房间信息
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("update")
    public Body update(Rooms rooms){
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean.getGame_userList().size()!=0){
            return Body.newInstance(451,"房间处于游戏状态，不可更改");
        }
        boolean b = rooms.updateById();
        if(b){
            Rooms select = rm.selectById(rooms.getId());
            roomBean.setDi_fen(select.getFen());
            roomBean.setJion_fen(select.getJionfen());
            roomBean.setMax_number(select.getMaxnumber());
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:删除房间信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("delete")
    public Body delete(Rooms rooms){
        RoomBean roomBean = Public_State.PKMap.get(String.valueOf(rooms.getRoomnumber()));
        if (roomBean.getGame_userList().size()!=0){
            return Body.newInstance(451,"房间处于游戏状态，不可删除");
        }
        boolean b = rooms.deleteById();
        if(b){
            Public_State.PKMap.remove(String.valueOf(rooms.getRoomnumber()));
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:添加房间信息
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("insert")
    public Body insert(Rooms rooms){
        rooms.setRoomnumber(Integer.valueOf(String.valueOf((Math.random()*9+1)*100000)));
        rooms.setCreatetime(new Date());
        boolean b = rooms.insert();
        if(b){
            RoomBean roomBean = CreatRoom.EcoSocket(String.valueOf(rooms.getRoomnumber()));
            roomBean.setMax_number(rooms.getMaxnumber());
            roomBean.setJion_fen(rooms.getJionfen());
            roomBean.setDi_fen(rooms.getFen());
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }
}


package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.game_bean.Public_State;
import com.zcf.game_bean.RoomBean;
import com.zcf.game_bean.UserBean;
import com.zcf.game_center.PK_WebSocket;
import com.zcf.mapper.RobotMapper;
import com.zcf.pojo.Robot;
import com.zcf.util.Body;
import com.zcf.util.FileService;
import com.zcf.util.LayuiJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-21
 */
@RestController
@CrossOrigin
@RequestMapping("/robot")
public class RobotController {

    @Autowired
    RobotMapper rm;

    @Autowired
    FileService iFileService;

    /**
     *@ Author:ZhaoQi
     *@ methodName:获取机器人列表
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @GetMapping("get")
    public LayuiJson get(@RequestParam(value = "pageNum") int pageNum,
                         @RequestParam(value = "pageSize") int pageSize){
        LayuiJson lj = new LayuiJson();
        EntityWrapper<Robot> ew = new EntityWrapper<>();
        ew.orderBy("id",false);
        List<Robot> list = rm.selectPage(new Page<Robot>(pageNum, pageSize), ew);
        Integer count = rm.selectCount(null);
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
     *@ methodName:
     *@ Params:删除机器人信息
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("delete")
    public Body delete(Robot robot){
        boolean b = robot.deleteById();
        if(b){
            for(Map.Entry<String, RoomBean> entry : Public_State.PKMap.entrySet()){
                RoomBean r = entry.getValue();
                List<UserBean> list = r.getGame_userList(0);
                if(list.size()!=0){
                    for (UserBean user :
                            list) {
                        if (user.getUserid()==robot.getId()) {
                            user.setGametype(2);//标记掉线，游戏结束自动退出
                        }
                    }
                }
            }
            return  Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:修改
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/22
     */
    @PostMapping("update")
    public Body update(Robot robot){
        boolean b = robot.updateById();
        if(b){
            Robot select = robot.selectById();
            for(Map.Entry<String, RoomBean> entry : Public_State.PKMap.entrySet()){
                RoomBean r = entry.getValue();
                List<UserBean> list = r.getGame_userList(0);
                if(list.size()!=0){
                    for (UserBean user :
                            list) {
                        if (user.getUserid()==select.getId()) {
                            user.setNickname(select.getName());
                            user.setAvatarurl(select.getImg());
                            user.setWinodds(select.getWinodds());
                        }
                    }
                }
            }
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:添加机器人信息
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/10
     */
    @PostMapping("insert")
    public Body insert(Robot robot, @RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request){
        if(file!=null){
            String path = request.getSession().getServletContext().getRealPath("robot");
            String targetFileName = iFileService.upload(file,path);
            targetFileName = "/robot/"+targetFileName;
            robot.setImg("http://47.111.153.101:8080/cattle/"+targetFileName);
            //robot.setImg("http://localhost:8080/cattle/"+targetFileName);
            boolean b = robot.insert();
            if(b){
                return  Body.BODY_200;
            }
            return Body.BODY_451;
        }
        return Body.newInstance(451,"文件不能为空");
    }
}



package com.zcf.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zcf.game_bean.UserBean;
import com.zcf.mapper.CardtypeMapper;
import com.zcf.pojo.UserTable;
import com.zcf.mapper.UserTableMapper;
import com.zcf.service.UserTableService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zcf.util.Body;
import com.zcf.util.LayuiJson;
import com.zcf.util.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
@Service
public class UserTableServiceImpl extends ServiceImpl<UserTableMapper, UserTable> implements UserTableService {

    @Autowired
    UserTableMapper um;
    @Autowired
    CardtypeMapper cm;

    public UserBean selectByid(Long uid) {
        UserTable user = new UserTable();
        UserBean userBean = new UserBean();
        user.setUserid(uid);
        UserTable user1 = user.selectById();
        if(user1!=null){
            BeanUtils.copyProperties(user1,userBean);
            userBean.setUserid(Integer.valueOf(String.valueOf(uid)));
        }else{
            userBean = null;
        }
        return userBean;
    }

    public Body login(UserTable user) {
        EntityWrapper<UserTable> ew = new EntityWrapper<>();
        ew.eq("openid", user.getOpenid());
        UserTable userTable = user.selectOne(ew);
        if (userTable != null) {
            if(user.getAvatarurl()!=null){
                userTable.setAvatarurl(user.getAvatarurl());
            }
            if(user.getNickname()!=null){
                userTable.setNickname(user.getNickname());
            }
            userTable.updateById();
            if(userTable.getState()!=null && userTable.getState()==1){
                return Body.newInstance(0,"账号已被冻结，请联系管理员");
            }/*else if(user1.getIsLogin()!=null && user1.getIsLogin()==1){
                return Body.newInstance(451,"账号已在其他设备登录，如有疑问，请联系管理员");
            }*/
            return Body.newInstance(userTable);
        }else{
            user.setCreatetime(new Date());
            user.insert();
            EntityWrapper<UserTable> e = new EntityWrapper<>();
            e.eq("openid", user.getOpenid());
            UserTable users = user.selectOne(e);
            return Body.newInstance(users);
        }
    }


    public LayuiJson getUsers(UserTable user, int pageNum, int pageSize) {
        LayuiJson lj = new LayuiJson();
        Wrapper w = new EntityWrapper();
        List<String> list1 = new ArrayList<String>();
        list1.add("createtime");
        w.orderDesc(list1);
        if (user.getPhone()!=null&&user.getPhone()!=""){
            w.eq("phone",user.getPhone()).or().eq("nickname",user.getPhone()).or().eq("userid",user.getPhone());
        }
        if(user.getfId()!=null&&user.getfId()!=0){
            w.eq("fId",user.getfId());
        }
        List<UserTable> list = um.selectPage(new Page<UserTable>(pageNum, pageSize), w);
        Integer count = um.selectCount(w);
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

    public Body update(UserTable user) {
        if(user.getPassword()!=null){
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        }
        user.setMoney(user.getMoney()/100);
        boolean b = user.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public Body getUser(UserTable user) {
        UserTable user1 = user.selectById();
        if (user1 != null) {
            return Body.newInstance(user1);
        }
        return Body.BODY_451;
    }

    public String getCard_type() {
       return cm.getCard_type();
    }
}

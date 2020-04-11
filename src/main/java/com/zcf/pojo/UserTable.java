package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
public class UserTable extends Model<UserTable> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "userid", type = IdType.AUTO)
    private Long userid;
    private String openid;
    /**
     * 头像
     */
    private String avatarurl;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String phone;
    private String password;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 金币
     */
    private Double money;
    /**
     * 房卡
     */
    private Double diamond;
    /**
     * 账号状态 0正常 1冻结
     */
    private Integer state;
    /**
     * 是否已登录
     */
    @TableField("isLogin")
    private Integer isLogin;
    /**
     * 0 女  1男
     */
    private Integer sex;
    /**
     * 上级ID  0无上级
     */
    @TableField("fId")
    private Long fId;
    /**
     * 角色  0普通玩家   1推广员
     */
    private Integer role;
    /**
     * 6位邀请码
     */
    private String code;
    /**
     * 保险箱金额
     */
    private Double insure;
    /**
     * 支付宝账号
     */
    private String zfb;
    /**
     * 银行卡账号
     */
    private String bankcard;
    /**
     * 反水比例
     */
    private Integer backwater;
    /**
     * 胜率
     */
    private Integer winodds;

    public Integer getWinodds() {
        return winodds;
    }

    public void setWinodds(Integer winodds) {
        this.winodds = winodds;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getDiamond() {
        return diamond;
    }

    public void setDiamond(Double diamond) {
        this.diamond = diamond;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Integer isLogin) {
        this.isLogin = isLogin;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Long getfId() {
        return fId;
    }

    public void setfId(Long fId) {
        this.fId = fId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getInsure() {
        return insure;
    }

    public void setInsure(Double insure) {
        this.insure = insure;
    }

    public String getZfb() {
        return zfb;
    }

    public void setZfb(String zfb) {
        this.zfb = zfb;
    }

    public String getBankcard() {
        return bankcard;
    }

    public void setBankcard(String bankcard) {
        this.bankcard = bankcard;
    }

    public Integer getBackwater() {
        return backwater;
    }

    public void setBackwater(Integer backwater) {
        this.backwater = backwater;
    }

    @Override
    protected Serializable pkVal() {
        return this.userid;
    }

    @Override
    public String toString() {
        return "UserTable{" +
        "userid=" + userid +
        ", openid=" + openid +
        ", avatarurl=" + avatarurl +
        ", nickname=" + nickname +
        ", phone=" + phone +
        ", password=" + password +
        ", createtime=" + createtime +
        ", money=" + money +
        ", diamond=" + diamond +
        ", state=" + state +
        ", isLogin=" + isLogin +
        ", sex=" + sex +
        ", fId=" + fId +
        ", role=" + role +
        ", code=" + code +
        ", insure=" + insure +
        ", zfb=" + zfb +
        ", bankcard=" + bankcard +
        ", backwater=" + backwater +
        "}";
    }
}

package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
public class GameRecharge extends Model<GameRecharge> {

    private static final long serialVersionUID = 1L;

    /**
     * 充值提现记录表
     */
    @TableId(value = "rechargeid", type = IdType.AUTO)
    private Long rechargeid;
    /**
     * 玩家id
     */
    private Long userid;
    /**
     * 充值金额
     */
    private Double money;
    /**
     * 操作时间
     */
    private Date date;
    /**
     * 1充值记录  2提现记录
     */
    private Integer type;
    /**
     * 状态 0审核中 1已通过  2已拒绝
     */
    private Integer state;


    public Long getRechargeid() {
        return rechargeid;
    }

    public void setRechargeid(Long rechargeid) {
        this.rechargeid = rechargeid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    protected Serializable pkVal() {
        return this.rechargeid;
    }

    @Override
    public String toString() {
        return "GameRecharge{" +
        "rechargeid=" + rechargeid +
        ", userid=" + userid +
        ", money=" + money +
        ", date=" + date +
        ", type=" + type +
        ", state=" + state +
        "}";
    }
}

package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
public class Carry extends Model<Carry> {

    private static final long serialVersionUID = 1L;

    /**
     * 转账记录表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userid;
    private Long touser;
    private Date createtime;
    private Double money;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getTouser() {
        return touser;
    }

    public void setTouser(Long touser) {
        this.touser = touser;
    }

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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Carry{" +
        "id=" + id +
        ", userid=" + userid +
        ", touser=" + touser +
        ", createtime=" + createtime +
        ", money=" + money +
        "}";
    }
}

package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
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
public class PkRecordTable extends Model<PkRecordTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "recordid", type = IdType.AUTO)
    private Long recordid;
    /**
     * 用户id
     */
    private Integer userid;
    /**
     * 分数
     */
    private Integer number;
    /**
     * 余额
     */
    private String money;
    private Date createdate;


    public Long getRecordid() {
        return recordid;
    }

    public void setRecordid(Long recordid) {
        this.recordid = recordid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    @Override
    protected Serializable pkVal() {
        return this.recordid;
    }

    @Override
    public String toString() {
        return "PkRecordTable{" +
        "recordid=" + recordid +
        ", userid=" + userid +
        ", number=" + number +
        ", money=" + money +
        ", createdate=" + createdate +
        "}";
    }
}

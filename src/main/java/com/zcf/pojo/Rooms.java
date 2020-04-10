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
 * @since 2020-04-09
 */
public class Rooms extends Model<Rooms> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Date createtime;
    /**
     * 房间号
     */
    private Integer roomnumber;
    /**
     * 局数
     */
    private Integer maxnumber;
    /**
     * 底分
     */
    private Double fen;
    /**
     * 准入分
     */
    private Integer jionfen;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(Integer roomnumber) {
        this.roomnumber = roomnumber;
    }

    public Integer getMaxnumber() {
        return maxnumber;
    }

    public void setMaxnumber(Integer maxnumber) {
        this.maxnumber = maxnumber;
    }

    public Double getFen() {
        return fen;
    }

    public void setFen(Double fen) {
        this.fen = fen;
    }

    public Integer getJionfen() {
        return jionfen;
    }

    public void setJionfen(Integer jionfen) {
        this.jionfen = jionfen;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Rooms{" +
        "id=" + id +
        ", createtime=" + createtime +
        ", roomnumber=" + roomnumber +
        ", maxnumber=" + maxnumber +
        ", fen=" + fen +
        ", jionfen=" + jionfen +
        "}";
    }
}

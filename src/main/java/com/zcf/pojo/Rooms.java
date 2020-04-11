package com.zcf.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
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

    @TableField(exist = false)
    private int room_state;//当前房间状态  0空闲  1游戏中
    @TableField(exist = false)
    private int user_number;//当前房间人数
    @TableField(exist = false)
    private int game_number;//当前对战局数

    public int getGame_number() {
        return game_number;
    }

    public void setGame_number(int game_number) {
        this.game_number = game_number;
    }

    public int getRoom_state() {
        return room_state;
    }

    public void setRoom_state(int room_state) {
        this.room_state = room_state;
    }

    public int getUser_number() {
        return user_number;
    }

    public void setUser_number(int user_number) {
        this.user_number = user_number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
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

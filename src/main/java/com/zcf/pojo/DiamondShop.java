package com.zcf.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-07
 */
public class DiamondShop extends Model<DiamondShop> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private int state;

    @TableField(exist = false)
    private List<GameDiamondShop> list;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<GameDiamondShop> getList() {
        return list;
    }

    public void setList(List<GameDiamondShop> list) {
        this.list = list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DiamondShop{" +
        "id=" + id +
        ", name=" + name +
        "}";
    }
}

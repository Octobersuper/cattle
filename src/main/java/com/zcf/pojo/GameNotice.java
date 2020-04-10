package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
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
public class GameNotice extends Model<GameNotice> {

    private static final long serialVersionUID = 1L;

    /**
     * 公告表
     */
    @TableId(value = "noticeid", type = IdType.AUTO)
    private Long noticeid;
    /**
     * 公告内容
     */
    private String value;


    public Long getNoticeid() {
        return noticeid;
    }

    public void setNoticeid(Long noticeid) {
        this.noticeid = noticeid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Serializable pkVal() {
        return this.noticeid;
    }

    @Override
    public String toString() {
        return "GameNotice{" +
        "noticeid=" + noticeid +
        ", value=" + value +
        "}";
    }
}

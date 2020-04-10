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
public class GameService extends Model<GameService> {

    private static final long serialVersionUID = 1L;

    /**
     * 客服表
     */
    @TableId(value = "serviceid", type = IdType.AUTO)
    private Long serviceid;
    /**
     * 二维码
     */
    private String qq;
    /**
     * 微信号
     */
    private String wx;
    /**
     * 头像
     */
    private String img;
    /**
     * 客服链接
     */
    private String url;


    public Long getServiceid() {
        return serviceid;
    }

    public void setServiceid(Long serviceid) {
        this.serviceid = serviceid;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String vx) {
        this.wx = vx;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected Serializable pkVal() {
        return this.serviceid;
    }

    @Override
    public String toString() {
        return "GameService{" +
        "serviceid=" + serviceid +
        ", qq=" + qq +
        ", vx=" + wx +
        ", img=" + img +
        ", url=" + url +
        "}";
    }
}

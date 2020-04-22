package com.zcf.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zcf.pojo.Cardtype;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-15
 */
public interface CardtypeMapper extends BaseMapper<Cardtype> {

    String getCard_type();
}

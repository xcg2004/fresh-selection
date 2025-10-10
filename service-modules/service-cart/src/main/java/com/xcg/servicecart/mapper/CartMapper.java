package com.xcg.servicecart.mapper;

import com.xcg.freshcommon.domain.cart.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-09
 */
public interface CartMapper extends BaseMapper<Cart> {

    List<Cart> scrollPage(Integer pageSize, Long lastId, LocalDateTime lastCreateTime);
}

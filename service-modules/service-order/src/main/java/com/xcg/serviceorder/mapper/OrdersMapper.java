package com.xcg.serviceorder.mapper;

import com.xcg.freshcommon.domain.order.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
public interface OrdersMapper extends BaseMapper<Orders> {

    List<Orders> scrollPage(Integer pageSize, Long lastId, LocalDateTime lastCreateTime, Long userId);
}

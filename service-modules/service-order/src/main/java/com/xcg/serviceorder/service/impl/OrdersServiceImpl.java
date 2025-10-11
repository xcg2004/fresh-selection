package com.xcg.serviceorder.service.impl;

import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.serviceorder.mapper.OrdersMapper;
import com.xcg.serviceorder.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}

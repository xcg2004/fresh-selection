package com.xcg.serviceorder.service.impl;

import com.xcg.freshcommon.domain.orderItem.entity.OrderItem;
import com.xcg.serviceorder.mapper.OrderItemMapper;
import com.xcg.serviceorder.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单项表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-13
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}

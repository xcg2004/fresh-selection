package com.xcg.serviceorder.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollQueryParam;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.order.vo.OrderVO;
import com.xcg.freshcommon.enums.PayType;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
public interface IOrdersService extends IService<Orders> {

    Result<Long> create(List<OrderCreateDto> orderCreateDto, Long addressId, PayType payType);

    Result<OrderVO> selectById(Long orderId);

    Result<ScrollResultVO<OrderVO>> scrollPage(ScrollQueryParam scrollQueryParam);

    Result<Orders> getByOrderNo(String outTradeNo);

    Result<Boolean> updateStatusAndPayTime(String outTradeNo, Integer status);

    Result<Boolean> cancel(Long id);

    Result<Boolean> checkReceived(Long id);

    Result<Boolean> rebuy(Long id);
}

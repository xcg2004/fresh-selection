package com.xcg.servicecart.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollQueryParam;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.cart.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.cart.vo.CartVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-09
 */
public interface ICartService extends IService<Cart> {

    Result<Long> add(Long skuId, Integer quantity);

    Result<Boolean> updateQuantity(Long cartId, Integer quantity);

    Result<Boolean> deleteById(Long cartId);

    Result<Boolean> updateSelected(Long cartId);

    Result<Boolean> batchDelete(List<Long> cartIds);

    Result<ScrollResultVO<CartVO>> scrollPage(ScrollQueryParam scrollQueryParam);

    Result<Boolean> updateSpec(Long cartId, Long skuId);

    Result<Boolean> check(Long cartId, Long skuId, Integer quantity);
}

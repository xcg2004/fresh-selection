package com.xcg.servicecart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.core.utils.UserHolder;
import com.xcg.freshcommon.domain.cart.entity.Cart;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.feign.ProductFeignClient;
import com.xcg.servicecart.mapper.CartMapper;
import com.xcg.servicecart.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-09
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final ProductFeignClient productFeignClient;

    private final UserHolder userHolder;
    private final CartMapper cartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> add(Long skuId, Integer quantity) {
        Long userId = userHolder.getUserId();

        // 简单快速的库存检查（非强一致性）
        Result<Boolean> quickCheckResult = productFeignClient.checkStatusWithStock(skuId, quantity, false);
        if (!quickCheckResult.isSuccess()) {
            throw new BizException(quickCheckResult.getMessage());
        }

        // 使用数据库乐观锁处理购物车更新
        int retryCount = 3;

        for (int i = 0; i < retryCount; i++) {
            Cart current = query().eq("user_id", userId).eq("sku_id", skuId).one();

            if (current == null) {
                Cart cart = new Cart();
                cart.setUserId(userId);
                cart.setSkuId(skuId);
                cart.setQuantity(quantity);
                if (save(cart)) {
                    return Result.success(cart.getId());
                }
            } else {
                // 乐观锁更新
                boolean updated = update()
                        .setSql("quantity = quantity + " + quantity)
                        .eq("id", current.getId())
                        // 如果有版本号字段，使用版本号；否则使用原数量作为乐观锁条件
                        .eq("quantity", current.getQuantity())
                        .update();

                if (updated) {
                    return Result.success(current.getId());
                }

                // 更新失败，短暂等待后重试
                try {
                    Thread.sleep(50 * (i + 1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BizException("系统繁忙，请重试");
                }
            }
        }

        throw new BizException("添加购物车失败，请重试");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateQuantity(Long cartId, Integer quantity) {
        Cart byId = checkCartBelongUser(cartId);

        if (quantity < 1) {
            throw new BizException("数量不能小于1");
        }

        if(Objects.equals(byId.getQuantity(), quantity)){
            return Result.success(true);
        }

        Long skuId = byId.getSkuId();
        Result<Boolean> checkStatusWithStock = productFeignClient.checkStatusWithStock(skuId, quantity, false);
        if (!checkStatusWithStock.isSuccess()) {
            throw new BizException(checkStatusWithStock.getMessage());
        }

        //2.更新
        boolean update = update().set("quantity", quantity).eq("id", cartId).update();
        return update ? Result.success(true) : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteById(Long cartId) {
        checkCartBelongUser(cartId);
        boolean remove = removeById(cartId);
        if (remove) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateSelected(Long cartId) {
        Cart byId = checkCartBelongUser(cartId);
        boolean update = update().set("selected", byId.getSelected() == 1 ? 0 : 1).eq("id", cartId).update();
        if (!update) {
            throw new BizException("更新失败");
        }
        return Result.success(true);
    }

    private Cart checkCartBelongUser(Long cartId) {
        Long userId = userHolder.getUserId();

        Cart byId = query().eq("id", cartId).eq("user_id", userId).one();
        if (byId == null) {
            throw new BizException("购物车异常");
        }

        return byId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> batchDelete(List<Long> cartIds) {
        Long userId = userHolder.getUserId();

        List<Cart> carts = query().eq("user_id", userId).in("id", cartIds).list();
        if (carts == null || carts.size() != cartIds.size()) {
            throw new BizException("购物车异常");
        }

        boolean b = removeBatchByIds(carts);
        if (!b) {
            throw new BizException("删除失败");
        }
        return Result.success(true);
    }

    @Override
    public Result<ScrollResultVO<CartVO>> scrollPage(Integer pageSize, Long lastId, LocalDateTime lastCreateTime) {
        //1. 分页参数检查
        pageSize = checkPageSize(pageSize);
        //2. 分页查询(完成部分参数的构建)
        List<Cart> cartList = cartMapper.scrollPage(pageSize, lastId, lastCreateTime);
        //3. 构建VO
        List<CartVO> cartVOS = cartList.stream().map(this::convertToCartVO).toList();

        // 计算下次查询的游标
        Long nextCursor = null;
        if (!cartVOS.isEmpty()) {
            nextCursor = cartVOS.get(cartVOS.size() - 1).getId();
        }

        return Result.success(ScrollResultVO.of(cartVOS, nextCursor, pageSize));
    }

    private Integer checkPageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return 10;
        }
        if (pageSize > 100) {
            return 100;
        }
        return pageSize;
    }

    private CartVO convertToCartVO(Cart cart) {
        CartVO cartVO = new CartVO();

        cartVO.setId(cart.getId());
        cartVO.setSkuId(cart.getSkuId());
        cartVO.setQuantity(cart.getQuantity());
        cartVO.setSelected(cart.getSelected());
        cartVO.setCreateTime(cart.getCreateTime());

        // 处理其他字段
        Result<CartVO> booleanResult = productFeignClient.fillOtherFields(cartVO);
        if (!booleanResult.isSuccess()) {
            throw new BizException(booleanResult.getMessage());
        }

        return booleanResult.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateSpec(Long cartId, Long skuId) {
        //1.校验是否是当前用户的cart
        Cart byId = checkCartBelongUser(cartId);

        if(Objects.equals(byId.getSkuId(), skuId)){
            return Result.success(true);
        }

        //2.校验skuId是否与购物车中的skuId属于同一product
        Result<Product> currentCartProduct = productFeignClient.getProductBySkuId(byId.getSkuId());
        if(!currentCartProduct.isSuccess()){
            return Result.error(currentCartProduct.getMessage());
        }

        Result<Product> targetProduct = productFeignClient.getProductBySkuId(skuId);
        if(!targetProduct.isSuccess()){
            return Result.error(targetProduct.getMessage());
        }

        if(!currentCartProduct.getData().getId().equals(targetProduct.getData().getId())){
            return Result.error("请选择正确的商品");
        }

        byId.setSkuId(skuId);
        boolean update = updateById(byId);
        if(!update){
            throw new BizException("更新失败");
        }
        return Result.success(true);
    }

    @Override
    public Result<Boolean> check(Long cartId, Long skuId, Integer quantity) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Cart::getId, cartId)
                .eq(Cart::getSkuId, skuId)
                .eq(Cart::getUserId, userHolder.getUserId())
                .eq(Cart::getQuantity, quantity);
        Cart cart = getOne(queryWrapper);
        if(cart != null){
            return Result.success(true);
        }
        return Result.error("购物车信息有误");
    }
}

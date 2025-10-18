package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.freshcommon.fallback.OrderFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "service-order",
        path = "/api/orders",
        fallbackFactory = OrderFeignClientFallbackFactory.class,
        configuration = FeignConfig.class

)
public interface OrderFeignClient {

    @GetMapping("/getByOrderNo")
    Result<Orders> getByOrderNo(@RequestParam("outTradeNo") String outTradeNo);

    @PutMapping("/update/status-paytime")
    Result<Boolean> updateStatusAndPayTime(@RequestParam("outTradeNo") String outTradeNo,@RequestParam("status") Integer status);
}

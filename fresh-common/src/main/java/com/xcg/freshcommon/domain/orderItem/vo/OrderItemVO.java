package com.xcg.freshcommon.domain.orderItem.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("订单项VO")
public class OrderItemVO implements Serializable {

    private Long id;

    private Long orderId;

    private Long skuId;

    private String productName;

    private String skuSpec;

    private String skuImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;

    private LocalDateTime createTime;

}

package com.xcg.freshcommon.domain.orderItem.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("订单项ID")
    private Long id;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("SKU ID")
    private Long skuId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("SKU规格")
    private String skuSpec;

    @ApiModelProperty("SKU图片")
    private String skuImage;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("总价")
    private BigDecimal totalPrice;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}

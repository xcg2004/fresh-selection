package com.xcg.freshcommon.domain.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("订单创建参数")
public class OrderCreateDto implements Serializable {

    @ApiModelProperty("购物车id")
    @NotNull(message = "购物车id不能为空")
    private Long cartId;

    @ApiModelProperty("skuId")
    @NotNull(message = "skuId不能为空")
    private Long skuId;

    @ApiModelProperty("数量")
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @ApiModelProperty("备注")
    private String remark;
}

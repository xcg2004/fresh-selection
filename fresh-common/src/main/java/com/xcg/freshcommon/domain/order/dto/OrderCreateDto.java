package com.xcg.freshcommon.domain.order.dto;

import io.swagger.annotations.ApiModel;
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
    private Long id;
}

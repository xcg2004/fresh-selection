package com.xcg.freshcommon.domain.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcg.freshcommon.domain.orderItem.vo.OrderItemVO;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;
import com.xcg.freshcommon.enums.OrderStatus;
import com.xcg.freshcommon.enums.PayType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("订单VO")
public class OrderVO implements Serializable {

    @ApiModelProperty(value = "订单ID")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "实付金额")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "运费")
    private BigDecimal freightAmount;

    @ApiModelProperty(value = "支付方式：1-微信，2-支付宝")
    private PayType paymentType;

    @ApiModelProperty(value = "订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭")
    private OrderStatus status;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipTime;

    @ApiModelProperty(value = "确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    @ApiModelProperty(value = "订单项列表")
    private List<OrderItemVO> orderItemVOList;

    @ApiModelProperty(value = "收货地址VO")
    private UserAddressVO userAddressVO;

}

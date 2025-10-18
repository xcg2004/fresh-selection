package com.xcg.freshcommon.domain.payment.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcg.freshcommon.enums.PaymentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付记录表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("payment_record")
@ApiModel(value="PaymentRecord对象", description="支付记录表")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "支付流水号")
    private String paymentNo;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal paymentAmount;

    @ApiModelProperty(value = "支付方式：1-微信，2-支付宝")
    private PaymentType paymentType;

    @ApiModelProperty(value = "支付状态：0-待支付，1-支付成功，2-支付失败")
    private Integer paymentStatus;

    @ApiModelProperty(value = "第三方交易ID")
    private String thirdPartyTransactionId;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}

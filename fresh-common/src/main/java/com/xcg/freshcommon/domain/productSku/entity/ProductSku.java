package com.xcg.freshcommon.domain.productSku.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品SKU表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_sku")
@ApiModel(value="ProductSku对象", description="商品SKU表")
public class ProductSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品ID")
    private Long productId;

    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal price;

    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;

    @ApiModelProperty(value = "锁定库存")
    private Integer lockStock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "重量（kg）")
    private BigDecimal weight;

    @ApiModelProperty(value = "SKU图片")
    private String image;

    @ApiModelProperty(value = "规格展示文本（冗余字段）")
    private String specsText;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}

package com.xcg.freshcommon.domain.product.entity;

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
 * 商品SPU表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product")
@ApiModel(value="Product对象", description="商品SPU表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "主图URL")
    private String mainImage;

    @ApiModelProperty(value = "子图URL数组")
    private String subImages;

    @ApiModelProperty(value = "商品详情（HTML）")
    private String detail;

    @ApiModelProperty(value = "商品描述")
    private String description;

    @ApiModelProperty(value = "基础价格")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "状态：0-下架，1-上架")
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}

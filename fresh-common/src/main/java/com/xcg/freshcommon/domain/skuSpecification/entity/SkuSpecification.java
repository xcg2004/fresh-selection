package com.xcg.freshcommon.domain.skuSpecification.entity;

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
 * SKU规格组合表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sku_specification")
@ApiModel(value="SkuSpecification对象", description="SKU规格组合表")
public class SkuSpecification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "SKU ID")
    private Long skuId;

    @ApiModelProperty(value = "属性ID")
    private Long attributeId;

    @ApiModelProperty(value = "属性值ID")
    private Long attributeValueId;

    private LocalDateTime createTime;


}

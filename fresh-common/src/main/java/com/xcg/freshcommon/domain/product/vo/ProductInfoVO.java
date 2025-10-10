package com.xcg.freshcommon.domain.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("商品信息VO")
public class ProductInfoVO implements Serializable {
    @ApiModelProperty("商品ID")
    private Long id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("品牌ID")
    private Long brandId;

    @ApiModelProperty("主图URL")
    private String mainImage;

    @ApiModelProperty("基础价格")
    private BigDecimal basePrice;

    @ApiModelProperty("状态：0-下架，1-上架")
    private Integer status;

    @ApiModelProperty("商品SKU列表")
    private List<ProductScrollVO.ProductSkuVO> skuList;


}

package com.xcg.freshcommon.domain.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("商品滚动分页查询结果VO")
public class ProductScrollVO {

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

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("商品SKU列表")
    private List<ProductSkuVO> skuList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("商品SKU信息VO")
    public static class ProductSkuVO {

        @ApiModelProperty("SKU ID")
        private Long id;

        @ApiModelProperty("销售价格")
        private BigDecimal price;

        @ApiModelProperty("原价")
        private BigDecimal originalPrice;

        @ApiModelProperty("库存数量")
        private Integer stock;

        @ApiModelProperty("重量")
        private BigDecimal weight;

        @ApiModelProperty("图片")
        private String image;

        @ApiModelProperty("规格列表")
        private List<SkuSpecVO> specList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("SKU规格信息VO")
    public static class SkuSpecVO {

        @ApiModelProperty("属性ID")
        private Long attributeId;

        @ApiModelProperty("属性值ID")
        private Long attributeValueId;

        @ApiModelProperty("属性名称")
        private String attributeName;

        @ApiModelProperty("属性值名称")
        private String attributeValueName;
    }
}

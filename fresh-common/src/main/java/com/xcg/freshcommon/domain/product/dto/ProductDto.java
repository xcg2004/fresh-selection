package com.xcg.freshcommon.domain.product.dto;

import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@ApiModel("商品DTO")
public class ProductDto implements Serializable {

    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @ApiModelProperty(value = "三级分类ID", required = true)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @ApiModelProperty("商品品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "主图URL", required = true)
    @NotBlank(message = "主图不能为空")
    private String mainImage;

    @ApiModelProperty("商品子图")
    private List<String> subImages;

    @ApiModelProperty("商品详情")
    private String detail;

    @ApiModelProperty("商品描述")
    private String description;

    @ApiModelProperty(value = "基础价格", required = true)
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0", message = "基础价格不能为负")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "SKU列表", required = true)
    @NotNull(message = "SKUDto列表不能为空")
    @NotEmpty(message = "至少需要一个SKU")
    @Valid  // 嵌套校验SKU的字段
    private List<ProductSkuDto> skuList;
}

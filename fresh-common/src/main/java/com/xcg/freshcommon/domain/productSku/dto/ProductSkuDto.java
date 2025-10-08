package com.xcg.freshcommon.domain.productSku.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.DecimalMin;
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
@ApiModel("商品SKUDTO")
public class ProductSkuDto implements Serializable {

    @NotNull(message = "销售价格不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "销售价格必须大于0")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull(message = "库存数量不能为空")
    @DecimalMin(value = "0", message = "库存数量不能为负数")
    private Integer stock;

    private BigDecimal weight;

    private String image;

    @NotNull(message = "规格列表不能为空")
    @NotEmpty(message = "至少需要一个SKU规格")
    private List<SkuSpec> specList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SkuSpec{
        private Long attributeId;
        private Long attributeValueId;
    }
}

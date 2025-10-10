package com.xcg.freshcommon.domain.cart.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("购物车详情VO")
public class CartVO {

    @ApiModelProperty("购物车项ID")
    private Long id;

    @ApiModelProperty("商品SPU ID")
    private Long productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("商品SKU ID")
    private Long skuId;

    @ApiModelProperty("销售价格")
    private BigDecimal price;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("库存数量")
    private Integer stock;

    @ApiModelProperty("sku图片")
    private String image;

    @ApiModelProperty("状态：0-下架，1-上架")
    private Integer status;

    @ApiModelProperty("规格描述文本")
    private String specsText;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("是否选中：0-否，1-是")
    private Integer selected;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("规格详情列表")
    private List<ProductScrollVO.SkuSpecVO> specList;


}

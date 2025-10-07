package com.xcg.servicecategory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("分类DTO类")
public class CategoryDto implements Serializable {

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("父级分类ID")
    private Long parentId;

    @ApiModelProperty("图标")
    private String icon;
}

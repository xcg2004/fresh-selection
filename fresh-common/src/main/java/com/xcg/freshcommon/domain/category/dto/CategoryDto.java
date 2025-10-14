package com.xcg.freshcommon.domain.category.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @ApiModelProperty("父级分类ID")
    @Min(value = 0, message = "父级分类ID不能小于0")
    private Long parentId;

    @ApiModelProperty("图标")
    private String icon;
}

package com.xcg.freshcommon.domain.attribute.dto;

import com.xcg.freshcommon.domain.attributeValue.dto.AttributeValueDto;
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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("规格属性DTO")
public class AttributeDto implements Serializable {

    @ApiModelProperty(value = "属性名称，如：颜色、尺寸、重量")
    @NotBlank(message = "属性名称不能为空")
    private String name;

    @ApiModelProperty(value = "输入类型：1-单选，2-多选，3-文本输入")
    @Min(value = 1, message = "输入类型不能小于1")
    @Max(value = 3, message = "输入类型不能大于3")
    private Integer inputType;

    @ApiModelProperty(value = "是否可搜索：0-否，1-是")
    @Min(value = 0, message = "是否可搜索不能小于0")
    @Max(value = 1, message = "是否可搜索不能大于1")
    private Integer searchable;

    @ApiModelProperty(value = "是否必填：0-否，1-是")
    @Min(value = 0, message = "是否必填不能小于0")
    @Max(value = 1, message = "是否必填不能大于1")
    private Integer required;

    @ApiModelProperty(value = "属性值list")
    private List<AttributeValueDto> values;


}

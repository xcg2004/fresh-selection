package com.xcg.freshcommon.domain.attribute.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    private String name;

    @ApiModelProperty(value = "输入类型：1-单选，2-多选，3-文本输入")
    private Integer inputType;

    @ApiModelProperty(value = "是否可搜索：0-否，1-是")
    private Integer searchable;

    @ApiModelProperty(value = "是否必填：0-否，1-是")
    private Integer required;

    @ApiModelProperty(value = "属性值list")
    private List<AttributeValueDto> values;


}

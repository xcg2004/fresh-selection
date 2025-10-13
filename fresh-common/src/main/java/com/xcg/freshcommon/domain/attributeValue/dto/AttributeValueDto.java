package com.xcg.freshcommon.domain.attributeValue.dto;

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
@ApiModel("规格属性值DTO")
public class AttributeValueDto implements Serializable {

    @ApiModelProperty(value = "属性值")
    private String value;

    @ApiModelProperty(value = "扩展信息")
    private String extendedInfo;

}

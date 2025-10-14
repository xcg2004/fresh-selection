package com.xcg.freshcommon.domain.attributeValue.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("规格属性值DTO")
public class AttributeValueDto implements Serializable {

    @ApiModelProperty(value = "属性值")
    @NotBlank(message = "属性值不能为空")
    private String value;

    @ApiModelProperty(value = "扩展信息")
    private String extendedInfo;

}

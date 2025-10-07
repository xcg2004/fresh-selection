package com.xcg.freshcommon.domain.attribute.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规格属性值表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("attribute_value")
@ApiModel(value="AttributeValue对象", description="规格属性值表")
@Builder
public class AttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "属性ID")
    private Long attributeId;

    @ApiModelProperty(value = "属性值")
    private String value;

    @ApiModelProperty(value = "扩展信息，如图片、描述等")
    private String extendedInfo;

    private String a = "{\n" +
            "  \"color\": \"#FF0000\",\n" +
            "  \"hex\": \"#FF0000\"\n" +
            "}";

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}

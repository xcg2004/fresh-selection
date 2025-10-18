package com.xcg.freshcommon.domain.attribute.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规格属性表
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("attribute")
@ApiModel(value="Attribute对象", description="规格属性表")
@Builder
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "属性名称，如：颜色、尺寸、重量")
    private String name;

    @ApiModelProperty(value = "所属分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "输入类型：1-单选，2-多选，3-文本输入")
    private Integer inputType;

    @ApiModelProperty(value = "是否可搜索：0-否，1-是")
    private Integer searchable;

    @ApiModelProperty(value = "是否必填：0-否，1-是")
    private Integer required;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}

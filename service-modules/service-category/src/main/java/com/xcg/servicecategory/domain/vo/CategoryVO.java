package com.xcg.servicecategory.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("分类树VO类")
public class CategoryVO implements Serializable {
    @ApiModelProperty("分类ID")
    private Long id;

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("父级分类ID")
    private Long parentId;

    @ApiModelProperty("分类层级")
    private Integer level;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("排序")
    @JsonIgnore
    private Integer sort;

    @ApiModelProperty("子级分类")
    private List<CategoryVO> children;

}

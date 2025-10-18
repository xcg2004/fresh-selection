package com.xcg.freshcommon.core.utils;

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
@ApiModel(value = "分页查询通用结果类")
public class PageQueryResult<T> implements Serializable {

    @ApiModelProperty(value = "总记录数")
    private long total;

    @ApiModelProperty(value = "每页记录数")
    private long size;

    @ApiModelProperty(value = "当前页")
    private long current;

    @ApiModelProperty(value = "记录")
    private List<T> records;
}

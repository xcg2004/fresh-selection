package com.xcg.freshcommon.core.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("滚动分页查询参数")
public class ScrollQueryParam {
    
    @ApiModelProperty(value = "每页大小,默认20", example = "20")
    @Range(min = 1, max = 100, message = "每页大小必须在1-100之间")
    private Integer pageSize = 20;
    
    @ApiModelProperty(value = "最后一条记录ID，第一次查询不传", example = "12345")
    private Long lastId;
    
    @ApiModelProperty(value = "最后一条记录创建时间，第一次查询不传", example = "2025-10-14 10:30:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCreateTime;

    // 便捷的静态工厂方法
    public static ScrollQueryParam firstPage(Integer pageSize) {
        ScrollQueryParam param = new ScrollQueryParam();
        param.setPageSize(pageSize);
        return param;
    }

    // 判断是否是第一次查询
    public boolean isFirstPage() {
        return lastId == null && lastCreateTime == null;
    }

    // 获取有效的页面大小
    public Integer getValidPageSize() {
        return pageSize != null ? pageSize : 20;
    }
}
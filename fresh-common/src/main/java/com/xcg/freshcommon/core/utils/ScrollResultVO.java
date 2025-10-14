package com.xcg.freshcommon.core.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@ApiModel("滚动分页查询响应VO")
public class ScrollResultVO<T> {

    @ApiModelProperty("数据列表")
    private List<T> list;

    @ApiModelProperty("是否还有更多数据")
    private Boolean hasMore;

    @ApiModelProperty("下次查询的主键游标")
    private Long nextCursorId;

    @ApiModelProperty("下次查询的时间游标")
    private LocalDateTime nextCursorTime;

    @ApiModelProperty("当前页数据数量")
    private Integer size;

    public static <T> ScrollResultVO<T> of(List<T> list, Long nextCursorId,
                                           LocalDateTime nextCursorTime, Integer size) {
        ScrollResultVO<T> result = new ScrollResultVO<>();
        result.setList(list);
        result.setHasMore(list.size() >= size);
        result.setNextCursorId(nextCursorId);
        result.setNextCursorTime(nextCursorTime);
        result.setSize(list.size());
        return result;
    }
}

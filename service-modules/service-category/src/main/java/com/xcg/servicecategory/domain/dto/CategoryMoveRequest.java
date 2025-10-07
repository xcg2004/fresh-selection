package com.xcg.servicecategory.domain.dto;

import com.xcg.servicecategory.enums.MovePosition;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryMoveRequest {
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "移动位置不能为空")
    private MovePosition position;

    // 只有当position=AFTER时才需要
    private Long afterCategoryId;
}


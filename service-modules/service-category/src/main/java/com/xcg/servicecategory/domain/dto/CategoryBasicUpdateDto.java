package com.xcg.servicecategory.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryBasicUpdateDto {
    @NotNull(message = "分类ID不能为空")
    private Long id;
    
    private String name;
    private String icon;
    private Integer status;
}
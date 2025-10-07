package com.xcg.servicecategory.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.category.dto.CategoryBasicUpdateDto;
import com.xcg.freshcommon.domain.category.dto.CategoryDto;
import com.xcg.freshcommon.domain.category.dto.CategoryMoveRequest;
import com.xcg.freshcommon.domain.category.entity.Category;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.servicecategory.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-04
 */
@RestController
@RequestMapping("/api/category")
@Api(tags = "分类模块")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/tree")
    @ApiOperation("获取分类树")
    public Result<List<CategoryVO>> getTree() {
        return categoryService.getTree();
    }

    @GetMapping("/root/list")
    @ApiOperation("获取一级分类列表")
    public Result<List<CategoryVO>> getRootList() {
        return categoryService.getRootList();
    }

    @PostMapping("/create")
    @ApiOperation("创建分类")
    public Result<Long> create(@RequestBody CategoryDto categoryDto) {
        return categoryService.create(categoryDto);
    }

    /**
     * 更新分类基本信息
     */
    @PostMapping("/update-basic")
    public Result<Boolean> updateBasic(@RequestBody @Valid CategoryBasicUpdateDto dto) {
        log.info("更新分类基本信息: {}", dto);
        return categoryService.updateBasic(dto);
    }

    /**
     * 移动分类位置
     */
    @PostMapping("/move")
    public Result<Boolean> moveCategory(@RequestBody @Valid CategoryMoveRequest request) {
        log.info("移动分类位置: {}", request);
        return categoryService.moveCategory(request);
    }

    @PostMapping("/update/swap")
    @ApiOperation("交换分类排序")
    public Result<Boolean> updateSwap(Long firstId, Long secondId) {
        log.info("交换分类排序:{} {}", firstId, secondId);
        return categoryService.updateSwap(firstId, secondId);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除分类: {}", id);
        return categoryService.deleteById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取分类")
    public Result<CategoryVO> getById(@PathVariable Long id) {
        log.info("根据id获取分类: {}", id);
        return categoryService.selectById(id);
    }
}

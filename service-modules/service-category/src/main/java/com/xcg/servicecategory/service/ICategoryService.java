package com.xcg.servicecategory.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.category.dto.CategoryBasicUpdateDto;
import com.xcg.freshcommon.domain.category.dto.CategoryDto;
import com.xcg.freshcommon.domain.category.dto.CategoryMoveRequest;
import com.xcg.freshcommon.domain.category.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;

import java.util.List;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-04
 */
public interface ICategoryService extends IService<Category> {

    Result<List<CategoryVO>> getTree();

    Result<List<CategoryVO>> getRootList();

    Result<Long> create(CategoryDto categoryDto);

//    Result<Boolean> updateInfo(CategoryUpdateDto categoryUpdateDto);

    Result<Boolean> updateSwap(Long firstId, Long secondId);

    Result<Boolean> updateBasic(CategoryBasicUpdateDto dto);

    Result<Boolean> moveCategory(CategoryMoveRequest request);

    Result<Boolean> deleteById(Long id);

    Result<CategoryVO> selectById(Long id);
}

package com.xcg.servicecategory.mapper;

import com.xcg.freshcommon.domain.category.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 商品分类表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-04
 */
public interface CategoryMapper extends BaseMapper<Category> {

    void save(Category category);

    Category selectByIdForUpdate(Long firstId);

    void incrementRange(Long parentId, int start,int end,int increment);

    void updateSort(Long categoryId, int sort);

    Integer selectMaxSortByParentId(Long parentId);

    List<Long> lockByParentId(Long parentId);

}

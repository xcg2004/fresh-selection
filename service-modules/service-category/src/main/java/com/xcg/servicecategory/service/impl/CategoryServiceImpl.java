package com.xcg.servicecategory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.category.dto.CategoryBasicUpdateDto;
import com.xcg.freshcommon.domain.category.dto.CategoryDto;
import com.xcg.freshcommon.domain.category.dto.CategoryMoveRequest;
import com.xcg.freshcommon.domain.category.entity.Category;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.servicecategory.mapper.CategoryMapper;
import com.xcg.servicecategory.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryVO>> getTree() {
        // 查询所有分类
        List<Category> list = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .last("ORDER BY parent_id, sort, id")
        );

        // 将分类列表转换为VOMap
        Map<Long, CategoryVO> categoryVOMap = new HashMap<>(list.size());
        for (Category category : list) {
            CategoryVO vo = convertToVO(category);
            categoryVOMap.put(vo.getId(), vo);
        }

        // 构建树形结构
        List<CategoryVO> treeList = new ArrayList<>();

        for (CategoryVO categoryVO : categoryVOMap.values()) {
            // 根节点- 顶级分类
            if (categoryVO.getParentId() == 0) {
                treeList.add(categoryVO);
            } else {
                // 子节点
                CategoryVO parent = categoryVOMap.get(categoryVO.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(categoryVO);
                }
            }
        }

        // 递归排序整个树
        sortCategoryTree(treeList);

        return Result.success(treeList);
    }

    private void sortCategoryTree(List<CategoryVO> tree) {
        if (tree == null || tree.isEmpty()) {
            return;
        }
        tree.sort(Comparator.comparingInt(CategoryVO::getSort));
        for (CategoryVO node : tree) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortCategoryTree(node.getChildren());
            }
        }
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        vo.setLevel(category.getLevel());
        vo.setIcon(category.getIcon());
        vo.setSort(category.getSort());
        return vo;
    }

    @Override
    public Result<List<CategoryVO>> getRootList() {
        List<Category> list = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, 0)
                .eq(Category::getStatus, 1)
        );
        List<CategoryVO> rootList = new ArrayList<>(list.size());
        for (Category category : list) {
            CategoryVO vo = convertToVO(category);
            rootList.add(vo);
        }
        rootList.sort(Comparator.comparingInt(CategoryVO::getSort));
        return Result.success(rootList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> create(CategoryDto categoryDto) {
        // 1.参数校验
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            throw new BizException(400, "分类名称不能为空");
        }

        Long parentId = categoryDto.getParentId();
        Category category = Category.builder().build();

        if (parentId != null && parentId != 0) {
            // 子分类
            Category parent = getById(parentId);
            if (parent == null || parent.getStatus() != 1) {
                throw new BizException(400, "父级分类不存在或已删除");
            }
            category.setParentId(parentId);
            category.setLevel(parent.getLevel() + 1);

            // 获取同级别下的最大sort值
            Integer maxSort = getMaxSortByParentId(parentId);
            category.setSort(maxSort + 1);

        } else {
            // 顶级分类
            category.setParentId(0L); // 明确设置为0
            category.setLevel(1);     // 明确设置为1

            // 获取顶级分类的最大sort值
            Integer maxSort = getMaxSortByParentId(0L);
            category.setSort(maxSort + 1);
        }

        // 2.设置基础属性
        category.setName(categoryDto.getName().trim());
        category.setIcon(categoryDto.getIcon());

        // status和created_time等字段依赖数据库默认值

        // 3.插入
        categoryMapper.save(category);
        return Result.success(category.getId());
    }

    private Integer getMaxSortByParentId(Long parentId) {
        Category maxSortCategory = getOne(new LambdaQueryWrapper<Category>()
                .select(Category::getSort)
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)  // 只统计有效分类
                .orderByDesc(Category::getSort)
                .last("LIMIT 1"));

        // 如果没有找到记录，说明这是第一个子分类，应该返回-1，这样+1后就是0
        return maxSortCategory == null ? -1 : maxSortCategory.getSort();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateBasic(CategoryBasicUpdateDto dto) {
        Category existing = getById(dto.getId());
        if (existing == null) {
            throw new BizException(400, "分类不存在");
        }

        LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Category::getId, dto.getId());

        // 逐个字段检查并更新
        if (StringUtils.hasText(dto.getName())) {
            wrapper.set(Category::getName, dto.getName().trim());
        }

        if (StringUtils.hasText(dto.getIcon())) {
            wrapper.set(Category::getIcon, dto.getIcon().trim());
        }

        if (dto.getStatus() != null &&
                (dto.getStatus() == 0 || dto.getStatus() == 1)) {
            wrapper.set(Category::getStatus, dto.getStatus());
        }

        boolean updated = update(wrapper);
        if (updated) {
            log.info("分类基本信息更新成功: ID={}, Name={}", dto.getId(), dto.getName());
        }

        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> moveCategory(CategoryMoveRequest request) {
        Long categoryId = request.getCategoryId();
        Category movingCategory = getById(categoryId);

        if (movingCategory == null || movingCategory.getStatus() != 1) {
            throw new BizException(400, "分类不存在或已禁用");
        }

        return switch (request.getPosition()) {
            case FIRST -> moveToFirst(movingCategory);
            case LAST -> moveToLast(movingCategory);
            case AFTER -> moveAfter(movingCategory, request.getAfterCategoryId());
            default -> throw new BizException(400, "不支持的位置类型");
        };
    }

    /**
     * 移动到最前面
     */
    /**
     * 移动到最前面
     */
    private Result<Boolean> moveToFirst(Category movingCategory) {
        Long parentId = movingCategory.getParentId();
        Integer originalSort = movingCategory.getSort();

        // 如果已经在第一位，直接返回
        if (originalSort == 0) {
            log.info("分类已在最前面: {}[ID={}]", movingCategory.getName(), movingCategory.getId());
            return Result.success(true);
        }

        // 第一步：把X临时移到安全位置（避免影响其他分类）
        update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, movingCategory.getId())
                .set(Category::getSort, 999998));

        // 第二步：将第1位到原位置-1的所有分类后移
        categoryMapper.incrementRange(parentId, 0, originalSort - 1, 1);

        // 第三步：把X放到第1位
        update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, movingCategory.getId())
                .set(Category::getSort, 0));

        log.info("分类移动到最前面: {}[ID={}, Sort {}→1]",
                movingCategory.getName(), movingCategory.getId(), originalSort);
        return Result.success(true);
    }

    /**
     * 移动到最后面
     */
    private Result<Boolean> moveToLast(Category movingCategory) {
        Long parentId = movingCategory.getParentId();

        // 查询当前最大排序值
        Integer maxSort = categoryMapper.selectMaxSortByParentId(parentId);

        // 移动 [originSort+1, maxSort]  例如 0，1，2，3，4，5  move(0) -> 0后面的全部前移1位，0移动到最后面
        categoryMapper.incrementRange(parentId, movingCategory.getSort() + 1, maxSort, -1);

        update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, movingCategory.getId())
                .set(Category::getSort, maxSort));

        log.info("分类移动到最后面: {}[ID={}]", movingCategory.getName(), movingCategory.getId());

        return Result.success(true);
    }

    /**
     * 插入到指定分类后面
     */
    private Result<Boolean> moveAfter(Category movingCategory, Long afterCategoryId) {
        if (afterCategoryId == null) {
            throw new BizException(400, "AFTER位置必须指定afterCategoryId");
        }
        Long parentId = movingCategory.getParentId();
        Integer originSort = movingCategory.getSort();
        Category afterCategory = getById(afterCategoryId);
        Integer targetSort = afterCategory.getSort();

        // 层级校验
        if (!checkLevelAndParent(movingCategory.getId(), afterCategoryId)) {
            throw new BizException(400, "层级不一致");
        }

        // 边界检查
        if (originSort.equals(targetSort)) {
            throw new BizException(400, "不能插入到自身后面");
        }

        // 加锁防止并发冲突
        categoryMapper.lockByParentId(parentId);

        // 情况1: 当前分类在目标分类前面 (origin < target)
        if (originSort < targetSort) {
            // 1. 移走当前分类
            categoryMapper.updateSort(movingCategory.getId(), 999999);

            // 2. 将(origin+1, target]区间的分类前移一位
            categoryMapper.incrementRange(parentId, originSort + 1, targetSort, -1);

            // 3. 将当前分类放到目标位置
            categoryMapper.updateSort(movingCategory.getId(), targetSort);
        }
        // 情况2: 当前分类在目标分类后面 (origin > target)
        else {
            // 1. 移走当前分类
            categoryMapper.updateSort(movingCategory.getId(), 999999);

            // 2. 将(target+1, origin-1]区间的分类后移一位
            categoryMapper.incrementRange(parentId, targetSort + 1, originSort - 1, 1);

            // 3. 将当前分类放到目标后面
            categoryMapper.updateSort(movingCategory.getId(), targetSort + 1);
        }

        log.info("分类移动: {}[ID={}, Sort {}→{}] 到 {}[ID={}] 后面",
                movingCategory.getName(), movingCategory.getId(), originSort,
                (originSort < targetSort) ? targetSort : targetSort + 1,
                afterCategory.getName(), afterCategory.getId());

        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateSwap(Long firstId, Long secondId) {
        // 先校验
        if (!checkLevelAndParent(firstId, secondId)) {
            throw new BizException(400, "层级不一致");
        }

        Category first = getById(firstId);
        Category second = getById(secondId);

        // 使用悲观锁防止并发问题
        Category firstLocked = categoryMapper.selectByIdForUpdate(firstId);
        Category secondLocked = categoryMapper.selectByIdForUpdate(secondId);

        // 直接交换，因为已经加了锁
        update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, firstId)
                .set(Category::getSort, second.getSort()));

        update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, secondId)
                .set(Category::getSort, first.getSort()));

        log.info("分类交换成功: {}[{}] <-> {}[{}]",
                first.getName(), first.getSort(),
                second.getName(), second.getSort());

        return Result.success(true);
    }

    private Boolean checkLevelAndParent(Long firstId, Long secondId) {
        Category firstCategory = getById(firstId);
        Category secondCategory = getById(secondId);

        if (firstCategory == null || secondCategory == null) {
            return false;
        }

        return Objects.equals(firstCategory.getLevel(), secondCategory.getLevel())
                && Objects.equals(firstCategory.getParentId(), secondCategory.getParentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteById(Long id) {
        // 1. 检查分类是否存在
        Category category = getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }

        // 2. 检查子分类（加锁前先快速检查）
        long childCount = query().eq("parent_id", id).count();
        if (childCount > 0) {
            return Result.error("请先删除子分类");
        }

        // 3. 加锁（锁定所有兄弟节点）
        Long parentId = category.getParentId();
        categoryMapper.lockByParentId(parentId);


        // 4. 二次校验（防止加锁前数据变化）
        childCount = query().eq("parent_id", id).count();
        if (childCount > 0) {
            throw new BizException("存在子分类，请刷新后重试");
        }

        // 5. 获取最新数据（避免脏读）
        category = getById(id);
        if (category == null) {
            return Result.error("分类已被删除");
        }

        // 6. 获取最大排序值（排除当前分类）
        Integer maxSort = categoryMapper.selectMaxSortByParentId(parentId);

        // 7. 调整排序（如果不是最后一个）
        if (maxSort != null && category.getSort() < maxSort) {
            categoryMapper.incrementRange(parentId, category.getSort() + 1, maxSort, -1);
        }

        // 8. 执行删除
        boolean success = removeById(id);
        return Result.success(success);
    }

    @Override
    public Result<CategoryVO> selectById(Long id) {
        // 检查分类是否存在
        Category category = query().eq("id", id).eq("status", 1).one();
        if (category == null) {
            return Result.error("分类不存在或已禁用");
        }

        // 转换为VO
        CategoryVO categoryVO = convertToVO(category);

        // 查询子分类
        List<Category> childCategories = query()
                .eq("parent_id", id)
                .eq("status", 1)
                .orderByAsc("sort")
                .list();

        if (!childCategories.isEmpty()) {
            // 如果有子分类，转换为VO列表
            List<CategoryVO> childVOs = childCategories.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            categoryVO.setChildren(childVOs);
        }
        // 如果没有子分类，children保持为null，hasChildren()会返回false

        return Result.success(categoryVO);
    }
}

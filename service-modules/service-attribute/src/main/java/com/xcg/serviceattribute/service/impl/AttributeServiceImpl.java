package com.xcg.serviceattribute.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.attribute.dto.AttributeDto;
import com.xcg.freshcommon.domain.attributeValue.dto.AttributeValueDto;
import com.xcg.freshcommon.domain.attribute.entity.Attribute;
import com.xcg.freshcommon.domain.attributeValue.entity.AttributeValue;
import com.xcg.freshcommon.domain.attribute.vo.AttributeVO;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.freshcommon.feign.CategoryFeignClient;
import com.xcg.serviceattribute.mapper.AttributeMapper;
import com.xcg.serviceattribute.mapper.AttributeValueMapper;
import com.xcg.serviceattribute.service.IAttributeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 规格属性表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
@Service
@RequiredArgsConstructor
public class AttributeServiceImpl extends ServiceImpl<AttributeMapper, Attribute> implements IAttributeService {

    private final AttributeValueMapper attributeValueMapper;

    private final CategoryFeignClient categoryFeignClient;

    private final StringRedisTemplate redisTemplate;

    @Override
    public Result<List<AttributeVO>> listWithValue(long categoryId) {
        // 查询指定分类下状态为1的属性，并按排序字段升序排列
        List<Attribute> attributes = list(new LambdaQueryWrapper<Attribute>()
                .eq(Attribute::getCategoryId, categoryId)
                .eq(Attribute::getStatus, 1)
                .orderBy(true, true, Attribute::getSort)
        );

        // 将属性转换为VO并建立ID映射关系
        Map<Long, AttributeVO> attributeMap = new HashMap<>();

        for (Attribute attribute : attributes) {
            AttributeVO vo = convertToVO(attribute);
            attributeMap.put(attribute.getId(), vo);
        }

        // 批量查询所有属性对应的属性值
        Set<Long> list = attributes.stream().map(Attribute::getId).collect(Collectors.toSet());
        if (list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<AttributeValue> attributeValues = attributeValueMapper.selectList(new LambdaQueryWrapper<AttributeValue>()
                .in(AttributeValue::getAttributeId, list)
                .eq(AttributeValue::getStatus, 1)
                .orderBy(true, true, AttributeValue::getSort)
        );

        // 将属性值关联到对应的属性VO中
        for (AttributeValue attributeValue : attributeValues) {
            Long attributeId = attributeValue.getAttributeId();
            AttributeVO vo = attributeMap.get(attributeId);
            List<AttributeVO.AttributeValueVO> values = vo.getValues();

            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(AttributeVO.AttributeValueVO.builder()
                    .id(attributeValue.getId())
                    .value(attributeValue.getValue())
                    .extendedInfo(attributeValue.getExtendedInfo())
                    .sort(attributeValue.getSort())
                    .build());
            vo.setValues(values);

        }

        // 按排序字段对结果进行排序并返回
        List<AttributeVO> collect = attributeMap.values().stream().sorted(Comparator.comparing(AttributeVO::getSort)).collect(Collectors.toList());

        return Result.success(collect);
    }


    private AttributeVO convertToVO(Attribute attribute) {
        return AttributeVO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .sort(attribute.getSort())
                .required(attribute.getRequired())
                .searchable(attribute.getSearchable())
                .inputType(attribute.getInputType())
                .build();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> createWithValues(long categoryId, List<AttributeDto> attributeDtoList) {
        for (AttributeDto dto : attributeDtoList) {
            if (dto.getValues() == null || dto.getValues().isEmpty()) {
                return Result.error("请填写属性值");
            }
            if (dto.getValues().size() > 20) {
                return Result.error("属性值不能超过20个");
            }
        }

        // 使用分布式锁防止并发创建
        String lockKey = "attribute:lock:" + categoryId;
        try {
            Boolean b = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(b)) {
                return Result.error("操作过于频繁，请稍后再试");
            }

            // 检查分类
            Result<CategoryVO> categoryVOResult = categoryFeignClient.getById(categoryId);
            if (!categoryVOResult.isSuccess()) {
                return Result.error(categoryVOResult.getMessage());
            }
            CategoryVO categoryVO = categoryVOResult.getData();
            if (categoryVO.hasChildren()) {
                return Result.error("只有叶子分类才能添加属性值");
            }
            // 检查分类下是否已存在属性
            List<Attribute> list = query().eq("category_id", categoryId).list();
            if (list != null && !list.isEmpty()) {
                return Result.error("该分类下已存在属性");
            }

            List<Attribute> attributes = new ArrayList<>();

            for (int i = 0; i < attributeDtoList.size(); i++) {
                AttributeDto dto = attributeDtoList.get(i);
                Attribute attribute = Attribute.builder()
                        .name(dto.getName())
                        .categoryId(categoryId)
                        .inputType(dto.getInputType())
                        .searchable(dto.getSearchable())
                        .required(dto.getRequired())
                        .sort(i)
                        .build();
                attributes.add(attribute);
            }

            saveBatch(attributes);

            // 为每个属性添加对应的属性值
            List<AttributeValue> allAttributeValues = new ArrayList<>();
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attribute = attributes.get(i);
                AttributeDto attributeDto = attributeDtoList.get(i);

                // 获取该属性对应的属性值列表
                List<AttributeValueDto> values = attributeDto.getValues();
                if (values != null && !values.isEmpty()) {
                    for (int j = 0; j < values.size(); j++) {
                        AttributeValueDto valueDto = values.get(j);
                        AttributeValue attributeValue = AttributeValue.builder()
                                .attributeId(attribute.getId()) // 使用保存后生成的ID
                                .value(valueDto.getValue())
                                .extendedInfo(JSON.toJSONString(valueDto.getExtendedInfo()))
                                .sort(j) // 从0开始排序
                                .build();
                        allAttributeValues.add(attributeValue);
                    }
                }
            }

            // 批量保存所有属性值
            if (!allAttributeValues.isEmpty()) {
                attributeValueMapper.insertBatchSomeColumn(allAttributeValues);
            }

            return Result.success(true);
        } finally {
            redisTemplate.delete(lockKey);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> addValues(Long id, List<AttributeValueDto> attributeValueDtoList) {
        // 查询指定ID的属性
        Attribute byId = query().eq("id", id).eq("status", 1).one();
        if (byId == null) {
            return Result.error("属性不存在或已禁用");
        }
        // 检查分类状态
        Result<CategoryVO> categoryVOResult = categoryFeignClient.getById(byId.getCategoryId());
        if (!categoryVOResult.isSuccess()) {
            return Result.error(categoryVOResult.getMessage());
        }
        CategoryVO categoryVO = categoryVOResult.getData();
        if (categoryVO.hasChildren()) {
            return Result.error("只有叶子分类才能添加属性值");
        }

        Integer maxSort = attributeValueMapper.selectMaxSort(id);
        List<AttributeValue> values = new ArrayList<>();
        for (AttributeValueDto attributeValueDto : attributeValueDtoList) {
            AttributeValue attributeValue = AttributeValue.builder()
                    .attributeId(id)
                    .value(attributeValueDto.getValue())
                    .extendedInfo(JSON.toJSONString(attributeValueDto.getExtendedInfo()))
                    .sort(maxSort + 1)
                    .build();
            values.add(attributeValue);
            maxSort += 1;
        }
        attributeValueMapper.insertBatchSomeColumn(values);

        return Result.success(true);
    }

    @Override
    public Result<List<Long>> getAttrIdsByCategoryId(Long categoryId) {
        List<Long> list = query().eq("category_id", categoryId).eq("status", 1).list()
                .stream()
                .map(Attribute::getId)
                .toList();
        return Result.success(list);
    }

    @Override
    public Result<Boolean> checkAttributeValueValid(Long attributeId, Long attributeValueId) {
        AttributeValue byId = attributeValueMapper.selectById(attributeValueId);
        if (byId == null) {
            return Result.error("属性值不存在");
        }
        if (!attributeId.equals(byId.getAttributeId())) {
            return Result.error("属性值ID=" + attributeValueId + "不属于属性ID=" + attributeId);
        }
        return Result.success(true);
    }

    @Override
    public Result<String> getAttributeName(Long attributeId) {
        Attribute byId = query().eq("id", attributeId).eq("status", 1).one();
        if(byId == null){
            return Result.error("属性不存在或已禁用");
        }
        return Result.success(byId.getName());
    }

    @Override
    public Result<String> getAttributeValue(Long attributeValueId) {
        AttributeValue byId = attributeValueMapper.selectById(attributeValueId);
        if(byId == null){
            return Result.error("属性值不存在");
        }
        return Result.success(byId.getValue());
    }
}

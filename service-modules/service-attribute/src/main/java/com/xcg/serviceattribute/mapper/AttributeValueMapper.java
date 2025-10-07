package com.xcg.serviceattribute.mapper;

import com.xcg.freshcommon.domain.attribute.entity.AttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 规格属性值表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
public interface AttributeValueMapper extends BaseMapper<AttributeValue> {
    
    void insertBatchSomeColumn(List<AttributeValue> values);

    Integer selectMaxSort(Long attributeId);
}

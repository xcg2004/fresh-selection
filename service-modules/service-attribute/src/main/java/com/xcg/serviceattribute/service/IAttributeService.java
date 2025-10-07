package com.xcg.serviceattribute.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.attribute.dto.AttributeDto;
import com.xcg.freshcommon.domain.attribute.dto.AttributeValueDto;
import com.xcg.freshcommon.domain.attribute.entity.Attribute;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.attribute.vo.AttributeVO;

import java.util.List;

/**
 * <p>
 * 规格属性表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
public interface IAttributeService extends IService<Attribute> {

    List<AttributeVO> listWithValue(long categoryId);

    Result<Boolean> createWithValues(long categoryId, List<AttributeDto> attributeDto);

    Result<Boolean> addValues(Long id, List<AttributeValueDto> attributeValueDto);
}

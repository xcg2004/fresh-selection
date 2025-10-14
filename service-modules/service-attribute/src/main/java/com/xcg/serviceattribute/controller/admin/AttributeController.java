package com.xcg.serviceattribute.controller.admin;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.attribute.dto.AttributeDto;
import com.xcg.freshcommon.domain.attributeValue.dto.AttributeValueDto;
import com.xcg.serviceattribute.service.IAttributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 规格属性表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
@RestController(value = "管理端规格属性控制器")
@RequestMapping("/api/attribute/admin")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "管理端规格属性模块")
public class AttributeController {

    private final IAttributeService attributeService;

    @PostMapping("/create-with-values/{categoryId}")
    @ApiOperation("创建属性并添加初始属性值")
    public Result<Boolean> createWithValues(@PathVariable long categoryId,
                                            @RequestBody @Valid List<AttributeDto> attributeDto) {
        log.info("创建属性并添加初始属性值:{} {}", categoryId, attributeDto);
        return attributeService.createWithValues(categoryId, attributeDto);
    }

    @PostMapping("/{id}/values")
    @ApiOperation("为属性添加属性值")
    public Result<Boolean> addValues(@PathVariable Long id,
                                     @RequestBody List<AttributeValueDto> attributeValueDto) {
        log.info("为属性添加属性值:{} {}", id, attributeValueDto);
        return attributeService.addValues(id, attributeValueDto);
    }

}

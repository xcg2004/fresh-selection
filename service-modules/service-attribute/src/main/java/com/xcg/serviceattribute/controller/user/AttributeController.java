package com.xcg.serviceattribute.controller.user;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.attribute.vo.AttributeVO;
import com.xcg.serviceattribute.service.IAttributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 规格属性表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-07
 */
@RestController(value = "用户端规格属性控制器")
@RequestMapping("/api/attribute/user")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "用户端规格属性模块")
public class AttributeController {

    private final IAttributeService attributeService;

    @GetMapping("/category/{categoryId}")
    @ApiOperation("获取规格属性列表")
    public Result<List<AttributeVO>> list(@PathVariable long categoryId){
        return Result.success(attributeService.listWithValue(categoryId));
    }
}

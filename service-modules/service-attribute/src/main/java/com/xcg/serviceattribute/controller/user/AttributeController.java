package com.xcg.serviceattribute.controller.user;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.attribute.vo.AttributeVO;
import com.xcg.serviceattribute.service.IAttributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
        return attributeService.listWithValue(categoryId);
    }

    @GetMapping("/getAttrIds/{categoryId}")
    public Result<List<Long>> getAttrIdsByCategoryId(@PathVariable("categoryId") Long categoryId){
        return attributeService.getAttrIdsByCategoryId(categoryId);
    }

    @PostMapping("/checkAttributeValueValid")
    public Result<Boolean> checkAttributeValueValid(
            @RequestParam("attributeId") Long attributeId,
            @RequestParam("attributeValueId") Long attributeValueId){
        return attributeService.checkAttributeValueValid(attributeId, attributeValueId);
    }

    @GetMapping("/getAttributeName/{attributeId}")
    public Result<String> getAttributeName(@PathVariable("attributeId") Long attributeId){
        return attributeService.getAttributeName(attributeId);
    }

    @GetMapping("/getAttributeValue/{attributeValueId}")
    public Result<String> getAttributeValue(@PathVariable("attributeValueId") Long attributeValueId){
        return attributeService.getAttributeValue(attributeValueId);
    }
}

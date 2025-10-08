package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.fallback.AttributeFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 属性服务Feign客户端
 * 用于远程调用属性相关接口
 */
@FeignClient(
        name = "service-attribute",  // 对应属性服务的服务名
        path = "/api/attribute/user",     // 基础路径
        fallbackFactory = AttributeFeignClientFallbackFactory.class,  // 降级处理工厂
        configuration = FeignConfig.class  // Feign配置类
)
public interface AttributeFeignClient {

    /**
     * 根据分类ID获取属性ID列表
     * @param categoryId 分类ID
     * @return 该分类下的所有属性ID
     */
    @GetMapping("/getAttrIds/{categoryId}")
    Result<List<Long>> getAttrIdsByCategoryId(@PathVariable("categoryId") Long categoryId);

    /**
     * 校验属性值是否有效（是否属于指定属性）
     * @param attributeId 属性ID
     * @param attributeValueId 属性值ID
     * @return 校验结果：true-有效，false-无效
     */
    @PostMapping("/checkAttributeValueValid")
    Result<Boolean> checkAttributeValueValid(
            @RequestParam("attributeId") Long attributeId,
            @RequestParam("attributeValueId") Long attributeValueId
    );

    /**
     * 根据属性ID获取属性名称
     * @param attributeId 属性ID
     * @return 属性名称
     */
    @GetMapping("/getAttributeName/{attributeId}")
    Result<String> getAttributeName(@PathVariable("attributeId") Long attributeId);

    /**
     * 根据属性值ID获取属性值内容
     * @param attributeValueId 属性值ID
     * @return 属性值内容
     */
    @GetMapping("/getAttributeValue/{attributeValueId}")
    Result<String> getAttributeValue(@PathVariable("attributeValueId") Long attributeValueId);
}

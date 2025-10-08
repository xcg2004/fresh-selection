package com.xcg.serviceproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto;
import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto.SkuSpec;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;
import com.xcg.freshcommon.domain.skuSpecification.entity.SkuSpecification;
//import com.xcg.freshcommon.feign.BrandFeignClient;
import com.xcg.freshcommon.feign.AttributeFeignClient;
import com.xcg.freshcommon.feign.CategoryFeignClient;
import com.xcg.serviceproduct.mapper.ProductMapper;
import com.xcg.serviceproduct.service.IProductService;
import com.xcg.serviceproduct.service.IProductSkuService;
import com.xcg.serviceproduct.service.ISkuSpecificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品SPU表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    private final CategoryFeignClient categoryFeignClient;
    private final AttributeFeignClient attributeFeignClient;
//    private final BrandFeignClient brandFeignClient;
    private final IProductSkuService productSkuService;
    private final ISkuSpecificationService skuSpecificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createProduct(ProductDto productDto) {
        // 1. 校验SPU基础信息(已通过Spring-Validation完成)
//        validateSpuBasicInfo(productDto);

        // 2. 校验分类信息
        Long categoryId = productDto.getCategoryId();
        CategoryVO categoryVO = validateAndGetCategory(categoryId);

        // 3. 校验品牌信息
//        validateBrand(productDto.getBrandId());

        // 4. 创建SPU记录
        Product product = createProductEntity(productDto);
        if (!save(product)) {
            throw new BizException("商品保存失败");
        }

        // 5. 校验并处理SKU列表
        List<ProductSkuDto> skuList = productDto.getSkuList();
        validateSkuList(skuList);

        // 6. 获取当前分类下的有效属性ID
        List<Long> validAttrIds = getValidAttributeIds(categoryId);

        // 7. 处理SKU和规格信息
        List<ProductSku> productSkus = processSkus(product, skuList, validAttrIds);

        // 8. 批量保存SKU
        if (!productSkuService.saveBatch(productSkus)) {
            throw new BizException("SKU保存失败");
        }

        // 9. 处理并保存规格关联
        processAndSaveSkuSpecifications(productSkus, skuList);

        return Result.success(product.getId());
    }

    /**
     * 校验SPU基础信息
     */
    private void validateSpuBasicInfo(ProductDto productDto) {
        if (StringUtils.isBlank(productDto.getName())) {
            throw new BizException("商品名称不能为空");
        }
        if (StringUtils.isBlank(productDto.getMainImage())) {
            throw new BizException("商品主图不能为空");
        }
        if (productDto.getBasePrice() == null || productDto.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("基础价格不能为负数");
        }
    }

    /**
     * 校验并获取分类信息
     */
    private CategoryVO validateAndGetCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BizException("分类ID不能为空");
        }

        Result<CategoryVO> categoryVOResult = categoryFeignClient.getById(categoryId);
        if (!categoryVOResult.isSuccess()) {
            throw new BizException("获取分类信息失败：" + categoryVOResult.getMessage());
        }

        CategoryVO categoryVO = categoryVOResult.getData();
        if (categoryVO == null) {
            throw new BizException("分类不存在");
        }
        if (categoryVO.hasChildren()) {
            throw new BizException("请选择末级分类（无子分类的分类）");
        }

        return categoryVO;
    }

    /**
     * 校验品牌合法性
     */
//    private void validateBrand(Long brandId) {
//        if (brandId != null) {
//            Result<Boolean> brandValidResult = brandFeignClient.checkBrandExists(brandId);
//            if (!brandValidResult.isSuccess() || !brandValidResult.getData()) {
//                throw new BizException("品牌不存在或已禁用");
//            }
//        }
//    }

    /**
     * 创建商品实体
     */
    private Product createProductEntity(ProductDto productDto) {
        return Product.builder()
                .name(productDto.getName())
                .categoryId(productDto.getCategoryId())
                .brandId(productDto.getBrandId())
                .mainImage(productDto.getMainImage())
                .subImages(JSON.toJSONString(productDto.getSubImages()))
                .detail(productDto.getDetail())
                .description(productDto.getDescription())
                .basePrice(productDto.getBasePrice())
                .status(1) // 默认上架状态
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 校验SKU列表基本信息
     */
    private void validateSkuList(List<ProductSkuDto> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            throw new BizException("至少需要添加一个SKU");
        }
    }

    /**
     * 获取当前分类下的有效属性ID
     */
    private List<Long> getValidAttributeIds(Long categoryId) {
        Result<List<Long>> attrIdsResult = attributeFeignClient.getAttrIdsByCategoryId(categoryId);
        if (!attrIdsResult.isSuccess()) {
            throw new BizException("获取分类属性失败：" + attrIdsResult.getMessage());
        }

        List<Long> validAttrIds = attrIdsResult.getData();
        if (validAttrIds == null || validAttrIds.isEmpty()) {
            throw new BizException("当前分类未配置属性，请先配置分类属性");
        }

        return validAttrIds;
    }

    /**
     * 处理SKU信息
     */
    private List<ProductSku> processSkus(Product product, List<ProductSkuDto> skuList, List<Long> validAttrIds) {
        List<ProductSku> productSkus = new ArrayList<>();
        Set<String> specKeySet = new HashSet<>(); // 用于检测重复规格组合

        for (ProductSkuDto skuDto : skuList) {
            // 校验SKU基础信息(Spring-Validation解决)
//            validateSkuBasicInfo(skuDto);

            // 校验并获取规格信息
            List<SkuSpec> specList = skuDto.getSpecList();
            validateAndGetSpecList(specList, validAttrIds);

            // 生成规格组合key并检查重复
            String specKey = generateSpecKey(specList);
            if (specKeySet.contains(specKey)) {
                throw new BizException("存在重复的规格组合，请检查SKU配置");
            }
            specKeySet.add(specKey);

            // 构建SKU实体
            ProductSku productSku = buildProductSku(product, skuDto, specList);
            productSkus.add(productSku);
        }

        return productSkus;
    }

    /**
     * 校验SKU基础信息
     */
    private void validateSkuBasicInfo(ProductSkuDto skuDto) {
        if (skuDto.getPrice() == null || skuDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("SKU销售价必须大于0");
        }
        if (skuDto.getStock() == null || skuDto.getStock() < 0) {
            throw new BizException("SKU库存不能为负数");
        }
        if (skuDto.getSpecList() == null || skuDto.getSpecList().isEmpty()) {
            throw new BizException("SKU规格不能为空");
        }
    }

    /**
     * 校验规格列表
     */
    private void validateAndGetSpecList(List<SkuSpec> specList, List<Long> validAttrIds) {
        for (SkuSpec spec : specList) {
            // 校验属性ID有效性
            if (spec.getAttributeId() == null || !validAttrIds.contains(spec.getAttributeId())) {
                throw new BizException("属性ID=" + spec.getAttributeId() + "不属于当前分类或不存在");
            }

            // 校验属性值ID有效性
            if (spec.getAttributeValueId() == null) {
                throw new BizException("属性值ID不能为空");
            }

            Result<Boolean> valueValidResult = attributeFeignClient.checkAttributeValueValid(
                    spec.getAttributeId(), spec.getAttributeValueId());
            if (!valueValidResult.isSuccess() || !valueValidResult.getData()) {
                throw new BizException("属性值ID=" + spec.getAttributeValueId() + "无效或不属于属性ID=" + spec.getAttributeId());
            }
        }
    }

    /**
     * 生成规格组合唯一标识
     */
    private String generateSpecKey(List<SkuSpec> specList) {
        return specList.stream()
                .sorted((s1, s2) -> s1.getAttributeId().compareTo(s2.getAttributeId()))
                .map(spec -> spec.getAttributeId() + "_" + spec.getAttributeValueId())
                .collect(Collectors.joining("_"));
    }

    /**
     * 构建ProductSku实体
     */
    private ProductSku buildProductSku(Product product, ProductSkuDto skuDto, List<SkuSpec> specList) {
        // 生成规格展示文本
        String specsText = buildSpecsText(specList);

        // 生成SKU编码
        String skuCode = generateSkuCode(product.getId(), specList);

        return new ProductSku()
                .setProductId(product.getId())
                .setSkuCode(skuCode)
                .setPrice(skuDto.getPrice())
                .setOriginalPrice(skuDto.getOriginalPrice())
                .setStock(skuDto.getStock())
                .setLockStock(0)
                .setSales(0)
                .setWeight(skuDto.getWeight())
                .setImage(StringUtils.isNotBlank(skuDto.getImage()) ? skuDto.getImage() : product.getMainImage())
                .setSpecsText(specsText)
                .setStatus(1) // 默认启用状态
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
    }

    /**
     * 生成规格展示文本
     */
    private String buildSpecsText(List<SkuSpec> specList) {
        return specList.stream()
                .map(spec -> {
                    // 获取属性名称
                    Result<String> attrNameResult = attributeFeignClient.getAttributeName(spec.getAttributeId());
                    if (!attrNameResult.isSuccess()) {
                        throw new BizException("获取属性名称失败：" + attrNameResult.getMessage());
                    }

                    // 获取属性值
                    Result<String> valueResult = attributeFeignClient.getAttributeValue(spec.getAttributeValueId());
                    if (!valueResult.isSuccess()) {
                        throw new BizException("获取属性值失败：" + valueResult.getMessage());
                    }

                    return attrNameResult.getData() + "：" + valueResult.getData();
                })
                .collect(Collectors.joining("·"));
    }


    /**
     * 生成SKU编码
     */
    private String generateSkuCode(Long productId, List<SkuSpec> specList) {
        // 1. 生成规格组合的唯一标识
        String specKey = generateSpecKey(specList);
        // 2. 哈希+截断，避免过长
        String hexString = String.format("%06x", specKey.hashCode());
        String specHash = hexString.substring(0, 6);
        // 3. 最终编码：SPU_ID + 时间戳后4位 + 哈希
        return "SKU_" + productId + "_" + System.currentTimeMillis() % 10000 + "_" + specHash;
    }

    /**
     * 处理并保存SKU规格关联
     */
    private void processAndSaveSkuSpecifications(List<ProductSku> productSkus, List<ProductSkuDto> skuList) {
        List<SkuSpecification> skuSpecifications = new ArrayList<>();

        for (int i = 0; i < productSkus.size(); i++) {
            ProductSku productSku = productSkus.get(i);
            ProductSkuDto skuDto = skuList.get(i);
            List<SkuSpec> specList = skuDto.getSpecList();

            for (SkuSpec spec : specList) {
                SkuSpecification skuSpecification = new SkuSpecification()
                        .setSkuId(productSku.getId())
                        .setAttributeId(spec.getAttributeId())
                        .setAttributeValueId(spec.getAttributeValueId())
                        .setCreateTime(LocalDateTime.now());
                skuSpecifications.add(skuSpecification);
            }
        }

        if (!skuSpecifications.isEmpty() && !skuSpecificationService.saveBatch(skuSpecifications)) {
            throw new BizException("SKU规格关联保存失败");
        }
    }
}

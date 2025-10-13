package com.xcg.serviceproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.product.vo.ProductInfoVO;
import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;
import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto;
import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto.SkuSpec;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;
import com.xcg.freshcommon.domain.skuSpecification.entity.SkuSpecification;
//import com.xcg.freshcommon.feign.BrandFeignClient;
import com.xcg.freshcommon.feign.AttributeFeignClient;
import com.xcg.freshcommon.feign.CategoryFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcg.serviceproduct.mapper.ProductMapper;
import com.xcg.serviceproduct.service.IProductService;
import com.xcg.serviceproduct.service.IProductSkuService;
import com.xcg.serviceproduct.service.ISkuSpecificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    private final ProductMapper productMapper;
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
        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategoryId(productDto.getCategoryId());
        product.setBrandId(productDto.getBrandId());
        product.setMainImage(productDto.getMainImage());
        product.setSubImages(JSON.toJSONString(productDto.getSubImages()));
        product.setDetail(productDto.getDetail());
        product.setDescription(productDto.getDescription());
        product.setBasePrice(productDto.getBasePrice());
        product.setStatus(1); // 默认上架状态
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return product;
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

    @Override
    public Result<ScrollResultVO<ProductScrollVO>> scrollPage(Integer pageSize, Long lastId, LocalDateTime lastCreateTime) {
        pageSize = checkPageSize(pageSize);
        List<Product> products = productMapper.scrollPageByCursor(pageSize, lastId, lastCreateTime);

        // 转换为VO对象
        List<ProductScrollVO> productVOs = products.stream()
                .map(this::convertToProductScrollVO)
                .collect(Collectors.toList());

        // 计算下次查询的游标
        Long nextCursor = null;
        if (!productVOs.isEmpty()) {
            nextCursor = productVOs.get(productVOs.size() - 1).getId();
        }

        ScrollResultVO<ProductScrollVO> result = ScrollResultVO.of(productVOs, nextCursor, pageSize);
        return Result.success(result);
    }

    /**
     * 将Product实体转换为ProductScrollVO
     *
     * @param product 商品实体
     * @return ProductScrollVO
     */
    private ProductScrollVO convertToProductScrollVO(Product product) {
        ProductScrollVO productVO = new ProductScrollVO();
        productVO.setId(product.getId());
        productVO.setName(product.getName());
        productVO.setCategoryId(product.getCategoryId());
        productVO.setBrandId(product.getBrandId());
        productVO.setMainImage(product.getMainImage());
        productVO.setBasePrice(product.getBasePrice());
        productVO.setStatus(product.getStatus());
        productVO.setCreateTime(product.getCreateTime());

        // 查询关联的SKU信息
        LambdaQueryWrapper<ProductSku> skuQueryWrapper = new LambdaQueryWrapper<>();
        skuQueryWrapper.eq(ProductSku::getProductId, product.getId())
                .eq(ProductSku::getStatus, 1); // 只查询启用状态的SKU
        List<ProductSku> productSkus = productSkuService.list(skuQueryWrapper);

        // 转换SKU为VO
        List<ProductScrollVO.ProductSkuVO> skuVOs = productSkus.stream()
                .map(this::convertToProductSkuVO)
                .collect(Collectors.toList());

        productVO.setSkuList(skuVOs);
        return productVO;
    }

    /**
     * 将ProductSku实体转换为ProductScrollVO.ProductSkuVO
     *
     * @param productSku SKU实体
     * @return ProductScrollVO.ProductSkuVO
     */
    private ProductScrollVO.ProductSkuVO convertToProductSkuVO(ProductSku productSku) {
        ProductScrollVO.ProductSkuVO skuVO = new ProductScrollVO.ProductSkuVO();
        skuVO.setId(productSku.getId());
        skuVO.setPrice(productSku.getPrice());
        skuVO.setOriginalPrice(productSku.getOriginalPrice());
        skuVO.setStock(productSku.getStock());
        skuVO.setWeight(productSku.getWeight());
        skuVO.setImage(productSku.getImage());

        // 查询规格信息
        LambdaQueryWrapper<SkuSpecification> specQueryWrapper = new LambdaQueryWrapper<>();
        specQueryWrapper.eq(SkuSpecification::getSkuId, productSku.getId());
        List<SkuSpecification> skuSpecifications = skuSpecificationService.list(specQueryWrapper);

        // 转换规格为VO
        List<ProductScrollVO.SkuSpecVO> specVOs = skuSpecifications.stream()
                .map(this::convertToSkuSpecVO)
                .collect(Collectors.toList());

        skuVO.setSpecList(specVOs);
        return skuVO;
    }

    /**
     * 将SkuSpecification实体转换为ProductScrollVO.SkuSpecVO
     *
     * @param skuSpecification 规格实体
     * @return ProductScrollVO.SkuSpecVO
     */
    private ProductScrollVO.SkuSpecVO convertToSkuSpecVO(SkuSpecification skuSpecification) {
        ProductScrollVO.SkuSpecVO specVO = new ProductScrollVO.SkuSpecVO();
        specVO.setAttributeId(skuSpecification.getAttributeId());
        specVO.setAttributeValueId(skuSpecification.getAttributeValueId());

        // 获取属性和属性值名称
        try {
            Result<String> attrNameResult = attributeFeignClient.getAttributeName(skuSpecification.getAttributeId());
            if (attrNameResult.isSuccess()) {
                specVO.setAttributeName(attrNameResult.getData());
            }

            Result<String> attrValueResult = attributeFeignClient.getAttributeValue(skuSpecification.getAttributeValueId());
            if (attrValueResult.isSuccess()) {
                specVO.setAttributeValueName(attrValueResult.getData());
            }
        } catch (Exception e) {
            log.warn("获取属性或属性值名称失败: {}", e.getMessage());
        }

        return specVO;
    }

    private Integer checkPageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return 10;
        }
        if (pageSize > 100) {
            return 100;
        }
        return pageSize;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> batchChangeStatus(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Result.error("参数错误");
        }
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .in(Product::getId, productIds)
        );

        products.forEach(product -> {
            product.setStatus(product.getStatus() == 1 ? 0 : 1);
        });
        boolean b = updateBatchById(products);
        if (!b) {
            throw new BizException("批量修改商品状态失败");
        }
        return Result.success(true);
    }

    /**
     * 检查商品状态和库存
     *
     * @param skuId            SKU ID
     * @param quantity         数量
     * @param strictStockCheck 是否严格检查库存（true-下单用，false-加购用）
     */
    @Override
    public Result<Boolean> checkStatusWithStock(Long skuId, Integer quantity, Boolean strictStockCheck) {
        if (quantity <= 0) {
            return Result.error("购买数量必须大于0");
        }

        ProductSku productSku = productSkuService.getOne(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getId, skuId)
                .eq(ProductSku::getStatus, 1)
        );

        if (productSku == null) {
            return Result.error("商品SKU不存在或已下架");
        }

        // 库存检查策略
        if (strictStockCheck) {
            // 下单用：检查实际库存
            if (productSku.getStock() < quantity) {
                return Result.error("商品库存不足");
            }
        } else {
            // 加购用：只检查是否有库存(可选)
//            if (productSku.getStock() <= 0) {
//                return Result.error("商品已售罄");
//            }
        }

        // 校验商品状态
        Product product = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getId, productSku.getProductId())
                .eq(Product::getStatus, 1)
        );

        if (product == null) {
            return Result.error("商品不存在或已下架");
        }

        return Result.success(true);
    }


    @Override
    public Result<ProductScrollVO> getProductInfo(Long productId) {
        Product product = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getId, productId)
                .eq(Product::getStatus, 1)
        );
        if (product == null) {
            return Result.error("商品不存在或已下架");
        }
        ProductScrollVO productScrollVO = convertToProductScrollVO(product);
        return Result.success(productScrollVO);
    }

    @Override
    public Result<CartVO> fillOtherFields(CartVO cartVO) {
        Long skuId = cartVO.getSkuId();
        ProductSku productSku = productSkuService.getOne(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getId, skuId)
                .eq(ProductSku::getStatus, 1)
        );
        //1.productId
        cartVO.setProductId(productSku.getProductId());
        //2.price、originalPrice...
        cartVO.setPrice(productSku.getPrice());
        cartVO.setOriginalPrice(productSku.getOriginalPrice());
        cartVO.setStock(productSku.getStock());
        cartVO.setImage(productSku.getImage());
        cartVO.setStatus(productSku.getStatus());
        cartVO.setSpecsText(productSku.getSpecsText());
        //3.productName
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getId, productSku.getProductId())
                .eq(Product::getStatus, 1);
        Product product = getOne(queryWrapper);
        cartVO.setProductName(product.getName());
        //4.specList
        List<SkuSpecification> skuSpecifications = skuSpecificationService.list(new LambdaQueryWrapper<SkuSpecification>()
                .eq(SkuSpecification::getSkuId, skuId)
        );
        List<ProductScrollVO.SkuSpecVO> specVOList = skuSpecifications.stream().map(this::convertToSkuSpecVO).toList();
        cartVO.setSpecList(specVOList);
        return Result.success(cartVO);
    }

    @Override
    public Result<Product> getProductBySkuId(Long skuId) {
        Product product = productMapper.selectProductBySkuId(skuId);
        if (product == null) {
            return Result.error("未找到对应的商品信息");
        }
        return Result.success(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<ProductSku>> deductStock(Map<Long, Integer> skuIdAndQuantity) {
        if (skuIdAndQuantity.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<ProductSku> productSkus = productSkuService.list(new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getId, skuIdAndQuantity.keySet())
        );
        productSkus.forEach(productSku -> {
            Integer stock = productSku.getStock();
            if (stock - 1 <= 0) {
                throw new BizException("商品库存不足");
            }
            productSku.setStock(stock - 1);
            productSku.setLockStock(productSku.getLockStock() + 1);

        });
        productSkuService.updateBatchById(productSkus);
        return Result.success(productSkus);
    }

    @Override
    public Result<Boolean> batchCheckStatusWithStock(Map<Long, Integer> skuIdAndQuantity) {
        List<ProductSku> productSkus = productSkuService.list(new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getId, skuIdAndQuantity.keySet())
        );
        for (ProductSku productSku : productSkus) {
            if (productSku.getStock() < skuIdAndQuantity.get(productSku.getId())) {
                return Result.error("商品库存不足");
            }
            if (productSku.getStatus() != 1) {
                return Result.error("商品已下架");
            }
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> recoverStock(Map<Long, Integer> skuIdAndQuantity) {
        if (skuIdAndQuantity.isEmpty()) {
            return Result.success(true);
        }
        List<ProductSku> productSkus = productSkuService.list(new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getId, skuIdAndQuantity.keySet())
        );
        productSkus.forEach(productSku -> {
            productSku.setStock(productSku.getStock() + skuIdAndQuantity.get(productSku.getId()));
            productSku.setLockStock(productSku.getLockStock() - skuIdAndQuantity.get(productSku.getId()));
        });
        productSkuService.updateBatchById(productSkus);
        return Result.success(true);
    }
}
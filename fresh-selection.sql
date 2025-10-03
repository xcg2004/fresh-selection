-- =============================================
-- 生鲜甄选电商平台数据库设计
-- 数据库名: fresh_selection
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- =============================================

-- 创建数据库
DROP DATABASE IF EXISTS `fresh_selection`;
CREATE DATABASE `fresh_selection` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `fresh_selection`;

-- 关闭外键约束检查
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 用户相关表
-- =============================================

-- 用户表
CREATE TABLE `user`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`        VARCHAR(255) NOT NULL COMMENT '密码',
    `phone`           VARCHAR(20)  NOT NULL COMMENT '手机号',
    `email`           VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    `nickname`        VARCHAR(100)          DEFAULT NULL COMMENT '昵称',
    `avatar`          VARCHAR(500)          DEFAULT NULL COMMENT '头像URL',
    `gender`          TINYINT               DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday`        DATE                  DEFAULT NULL COMMENT '生日',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 用户地址表
CREATE TABLE `user_address`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL COMMENT '用户ID',
    `consignee`      VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    `phone`          VARCHAR(20)  NOT NULL COMMENT '收货电话',
    `province`       VARCHAR(50)           DEFAULT NULL COMMENT '省',
    `city`           VARCHAR(50)           DEFAULT NULL COMMENT '市',
    `district`       VARCHAR(50)           DEFAULT NULL COMMENT '区',
    `detail_address` VARCHAR(500) NOT NULL COMMENT '详细地址',
    `postal_code`    VARCHAR(20)           DEFAULT NULL COMMENT '邮政编码',
    `is_default`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户地址表';

-- =============================================
-- 2. 商品分类表
-- =============================================

-- 商品分类表
CREATE TABLE `category`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父级分类ID',
    `level`       TINYINT      NOT NULL DEFAULT 1 COMMENT '分类级别：1-一级，2-二级，3-三级',
    `icon`        VARCHAR(500)          DEFAULT NULL COMMENT '分类图标',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类表';

-- =============================================
-- 3. 规格系统表（核心）
-- =============================================

-- 规格属性表
CREATE TABLE `attribute`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL COMMENT '属性名称，如：颜色、尺寸、重量',
    `category_id` BIGINT       NOT NULL COMMENT '所属分类ID',
    `input_type`  TINYINT      NOT NULL DEFAULT 1 COMMENT '输入类型：1-单选，2-多选，3-文本输入',
    `searchable`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否可搜索：0-否，1-是',
    `required`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否必填：0-否，1-是',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    CONSTRAINT `fk_attribute_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='规格属性表';

-- 规格属性值表
CREATE TABLE `attribute_value`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `attribute_id`  BIGINT       NOT NULL COMMENT '属性ID',
    `value`         VARCHAR(200) NOT NULL COMMENT '属性值',
    `extended_info` JSON                  DEFAULT NULL COMMENT '扩展信息，如图片、描述等',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_attribute_id` (`attribute_id`),
    CONSTRAINT `fk_attribute_value_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attribute` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='规格属性值表';

-- =============================================
-- 4. 商品相关表
-- =============================================

-- 商品SPU表
CREATE TABLE `product`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `category_id` BIGINT         NOT NULL COMMENT '分类ID',
    `brand_id`    BIGINT                  DEFAULT NULL COMMENT '品牌ID',
    `main_image`  VARCHAR(500)            DEFAULT NULL COMMENT '主图URL',
    `sub_images`  JSON                    DEFAULT NULL COMMENT '子图URL数组',
    `detail`      TEXT COMMENT '商品详情（HTML）',
    `description` VARCHAR(500)            DEFAULT NULL COMMENT '商品描述',
    `base_price`  DECIMAL(10, 2) NOT NULL COMMENT '基础价格',
    `status`      TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品SPU表';

-- 商品SKU表
CREATE TABLE `product_sku`
(
    `id`             BIGINT         NOT NULL AUTO_INCREMENT,
    `product_id`     BIGINT         NOT NULL COMMENT '商品ID',
    `sku_code`       VARCHAR(100)   NOT NULL COMMENT 'SKU编码',
    `price`          DECIMAL(10, 2) NOT NULL COMMENT '销售价格',
    `original_price` DECIMAL(10, 2)          DEFAULT NULL COMMENT '原价',
    `stock`          INT            NOT NULL DEFAULT 0 COMMENT '库存数量',
    `lock_stock`     INT            NOT NULL DEFAULT 0 COMMENT '锁定库存',
    `sales`          INT            NOT NULL DEFAULT 0 COMMENT '销量',
    `weight`         DECIMAL(8, 3)           DEFAULT NULL COMMENT '重量（kg）',
    `image`          VARCHAR(500)            DEFAULT NULL COMMENT 'SKU图片',
    `specs_text`     VARCHAR(500)            DEFAULT NULL COMMENT '规格展示文本（冗余字段）',
    `status`         TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品SKU表';

-- SKU规格组合表
CREATE TABLE `sku_specification`
(
    `id`                 BIGINT   NOT NULL AUTO_INCREMENT,
    `sku_id`             BIGINT   NOT NULL COMMENT 'SKU ID',
    `attribute_id`       BIGINT   NOT NULL COMMENT '属性ID',
    `attribute_value_id` BIGINT   NOT NULL COMMENT '属性值ID',
    `create_time`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_attribute` (`sku_id`, `attribute_id`),
    KEY `idx_sku_id` (`sku_id`),
    CONSTRAINT `fk_spec_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_spec_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attribute` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_spec_attribute_value` FOREIGN KEY (`attribute_value_id`) REFERENCES `attribute_value` (`id`) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='SKU规格组合表';

-- 品牌表
CREATE TABLE `brand`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL COMMENT '品牌名称',
    `logo`        VARCHAR(500)          DEFAULT NULL COMMENT '品牌Logo',
    `description` VARCHAR(500)          DEFAULT NULL COMMENT '品牌描述',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='品牌表';

-- =============================================
-- 5. 购物车表
-- =============================================

-- 购物车表
CREATE TABLE `cart`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `sku_id`      BIGINT   NOT NULL COMMENT 'SKU ID',
    `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '购买数量',
    `selected`    TINYINT  NOT NULL DEFAULT 1 COMMENT '是否选中：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `sku_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车表';

-- =============================================
-- 6. 订单相关表
-- =============================================

-- 订单表
CREATE TABLE `orders`
(
    `id`              BIGINT         NOT NULL AUTO_INCREMENT,
    `order_no`        VARCHAR(64)    NOT NULL COMMENT '订单号',
    `user_id`         BIGINT         NOT NULL COMMENT '用户ID',
    `total_amount`    DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `pay_amount`      DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    `freight_amount`  DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '运费',
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '优惠金额',
    `payment_type`    TINYINT        NOT NULL DEFAULT 1 COMMENT '支付方式：1-微信，2-支付宝',
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭',
    `address_id`      BIGINT         NOT NULL COMMENT '收货地址ID',
    `remark`          VARCHAR(500)            DEFAULT NULL COMMENT '订单备注',
    `create_time`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `pay_time`        DATETIME                DEFAULT NULL COMMENT '支付时间',
    `ship_time`       DATETIME                DEFAULT NULL COMMENT '发货时间',
    `confirm_time`    DATETIME                DEFAULT NULL COMMENT '确认收货时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_order_address` FOREIGN KEY (`address_id`) REFERENCES `user_address` (`id`) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

-- 订单项表
CREATE TABLE `order_item`
(
    `id`             BIGINT         NOT NULL AUTO_INCREMENT,
    `order_id`       BIGINT         NOT NULL COMMENT '订单ID',
    `sku_id`         BIGINT         NOT NULL COMMENT 'SKU ID',
    `product_name`   VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `sku_specs_text` VARCHAR(500)            DEFAULT NULL COMMENT 'SKU规格展示文本',
    `sku_image`      VARCHAR(500)            DEFAULT NULL COMMENT 'SKU图片',
    `price`          DECIMAL(10, 2) NOT NULL COMMENT '单价',
    `quantity`       INT            NOT NULL DEFAULT 1 COMMENT '数量',
    `total_price`    DECIMAL(10, 2) NOT NULL COMMENT '总价',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单项表';

-- 订单操作记录表
CREATE TABLE `order_operation_log`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT,
    `order_id`     BIGINT      NOT NULL COMMENT '订单ID',
    `operate_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `operate_note` VARCHAR(500)         DEFAULT NULL COMMENT '操作备注',
    `operate_user` VARCHAR(100)         DEFAULT NULL COMMENT '操作人',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_order_log_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单操作记录表';

-- =============================================
-- 7. 支付相关表
-- =============================================

-- 支付记录表
CREATE TABLE `payment_record`
(
    `id`                         BIGINT         NOT NULL AUTO_INCREMENT,
    `order_id`                   BIGINT         NOT NULL COMMENT '订单ID',
    `payment_no`                 VARCHAR(64)    NOT NULL COMMENT '支付流水号',
    `payment_amount`             DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    `payment_type`               TINYINT        NOT NULL DEFAULT 1 COMMENT '支付方式：1-微信，2-支付宝',
    `payment_status`             TINYINT        NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
    `third_party_transaction_id` VARCHAR(200)            DEFAULT NULL COMMENT '第三方交易ID',
    `pay_time`                   DATETIME                DEFAULT NULL COMMENT '支付时间',
    `create_time`                DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`                DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='支付记录表';

-- 退款记录表
CREATE TABLE `refund_record`
(
    `id`            BIGINT         NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT         NOT NULL COMMENT '订单ID',
    `refund_no`     VARCHAR(64)    NOT NULL COMMENT '退款单号',
    `refund_amount` DECIMAL(10, 2) NOT NULL COMMENT '退款金额',
    `refund_reason` VARCHAR(500)            DEFAULT NULL COMMENT '退款原因',
    `refund_status` TINYINT        NOT NULL DEFAULT 0 COMMENT '退款状态：0-申请中，1-退款成功，2-退款失败',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='退款记录表';

-- =============================================
-- 8. 库存管理系统
-- =============================================

-- 库存变更记录表
CREATE TABLE `inventory_change_log`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `sku_id`          BIGINT       NOT NULL COMMENT 'SKU ID',
    `change_type`     TINYINT      NOT NULL COMMENT '变更类型：1-入库，2-出库，3-锁定，4-解锁',
    `change_quantity` INT          NOT NULL COMMENT '变更数量',
    `current_stock`   INT          NOT NULL COMMENT '变更后当前库存',
    `source_type`     VARCHAR(50)  NOT NULL COMMENT '来源类型：ORDER-订单，RETURN-退货',
    `source_id`       VARCHAR(100) NOT NULL COMMENT '来源ID',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sku_id` (`sku_id`),
    CONSTRAINT `fk_inventory_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='库存变更记录表';

-- =============================================
-- 9. 消息系统表
-- =============================================

-- 消息发送记录表
CREATE TABLE `message_send_log`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `message_id`      VARCHAR(100) NOT NULL COMMENT '消息ID',
    `message_content` TEXT         NOT NULL COMMENT '消息内容',
    `exchange`        VARCHAR(200) NOT NULL COMMENT '交换机',
    `routing_key`     VARCHAR(200) NOT NULL COMMENT '路由键',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-发送中，1-发送成功，2-发送失败',
    `retry_count`     INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `next_retry_time` DATETIME              DEFAULT NULL COMMENT '下次重试时间',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_next_retry_time` (`next_retry_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='消息发送记录表';

-- =============================================
-- 10. 系统配置表
-- =============================================

-- 轮播图表
CREATE TABLE `banner`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(200) NOT NULL COMMENT '轮播图标题',
    `image_url`   VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_url`    VARCHAR(500)          DEFAULT NULL COMMENT '跳转链接',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='首页轮播图';

-- =============================================
-- 插入初始数据
-- =============================================

-- 插入用户数据
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `nickname`, `avatar`, `gender`)
VALUES ('user001', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', 'user001@example.com', '小明', '/avatars/default.png', 1),
       ('user002', 'e10adc3949ba59abbe56e057f20f883e', '13900139001', 'user002@example.com', '小红', '/avatars/default.png', 2);

-- 插入分类数据
INSERT INTO `category` (`name`, `parent_id`, `level`, `icon`, `sort`)
VALUES ('水果', 0, 1, '/icons/fruit.png', 1),
       ('蔬菜', 0, 1, '/icons/vegetable.png', 2),
       ('苹果', 1, 2, '/icons/apple.png', 11),
       ('香蕉', 1, 2, '/icons/banana.png', 12),
       ('叶菜类', 2, 2, '/icons/leafy.png', 21);

-- 插入规格属性数据
INSERT INTO `attribute` (`name`, `category_id`, `input_type`, `searchable`, `required`, `sort`)
VALUES ('颜色', 3, 1, 1, 1, 1),
       ('重量', 3, 1, 1, 1, 2),
       ('包装', 3, 1, 0, 1, 3);

-- 插入规格属性值数据
INSERT INTO `attribute_value` (`attribute_id`, `value`, `extended_info`)
VALUES (1, '红色', '{
  "color": "#FF0000",
  "hex": "#FF0000"
}'),
       (1, '绿色', '{
         "color": "#00FF00",
         "hex": "#00FF00"
       }'),
       (2, '500g', NULL),
       (2, '1kg', NULL),
       (3, '盒装', NULL),
       (3, '袋装', NULL);

-- 插入品牌数据
INSERT INTO `brand` (`name`, `logo`, `description`, `sort`)
VALUES ('优鲜果园', '/brands/yxgy.png', '优质水果供应商', 1),
       ('绿色农场', '/brands/lsnc.png', '有机蔬菜种植基地', 2);

-- 开启外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 创建索引优化查询性能
-- =============================================

-- 商品名称索引
CREATE INDEX `idx_product_name` ON `product` (`name`);
-- 订单状态索引
CREATE INDEX `idx_order_status` ON `orders` (`status`);
-- 支付状态索引
CREATE INDEX `idx_payment_status` ON `payment_record` (`payment_status`);
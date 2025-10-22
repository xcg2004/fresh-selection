# fresh-selection (生鲜甄选)

### 项目背景

fresh-selection 是一个用于**生鲜商品甄选**的微服务项目，旨在提供商品管理、分类、检索与简单筛选能力，方便在此基础上扩展电商、内容展示或供应链场景的功能。


### 功能 / Features

- 商品（Product、ProductSku）
- 分类（Category）
- 属性 (Attribute、AttributeValue)
- 购物车 (Cart)
- 订单 (Orders)
- 支付 (Payment)
- 用户 (User、UserAddress)

### 技术栈：SpringCloud全家桶、Mybatis-Plus、MySQL、Redis、RabbitMQ、Seata
### 构建工具：Maven
### 测试：JUnit、Apifox

### 环境要求 

- JDK 17 (SpringBoot 3.2要求)

- Maven

- MySQL、Redis

- RabbitMQ、Seata(2.0)、Nacos(2.3)、Sentinel(可选扩展)

### 快速开始 / Quick Start

###### 克隆仓库 / Clone

bash

git clone https://github.com/xcg2004/fresh-selection.git

##### 使用 Maven 构建 / Build with Maven

bash

cd fresh-selection

mvn clean package
###  运行（如果项目打包为可执行 jar）
java -jar target/fresh-selection-0.0.1-SNAPSHOT.jar

启动后访问 API（示例）
默认基路径（示例）：http://localhost:8080/api/products
具体接口请参考代码中的 Controller（或利用Apifox生成可视化文档）

仓库地址：https://github.com/xcg2004/fresh-selection

作者：xcg2004

# 校园二手交易平台 - 数据库课程设计

## 项目概述

校园二手交易平台是一个基于 **Spring Boot + Vue 3** 的前后端分离的二手交易网站。本项目是**数据库系统原理课程设计**，完整实现了数据库设计中的核心要求，包括冗余字段设计、索引优化、事务处理、触发器应用等。

---

## 课程设计要求对照

### 基础要求 ✅

| 要求 | 实现情况 | 说明 |
|------|---------|------|
| 完成数据库设计文档 | ✅ | 包含6张核心表、ER图、关系模型 |
| 建立数据库和表 | ✅ | 完整SQL脚本，一键创建 |
| 实现增删改查功能 | ✅ | 所有模块完整CRUD |
| 至少3张表有关联 | ✅ | user-product-order, user-cart-product等 |
| 使用事务保证数据一致性 | ✅ | 下单扣库存、取消订单恢复库存 |

### 进阶要求 ✅

| 要求 | 实现情况 | 说明 |
|------|---------|------|
| 索引优化 | ✅ | seller_id, buyer_id, status等字段建立索引 |
| 冗余字段设计 | ✅ | order.unit_price保存价格快照 |
| 数据库触发器 | ✅ | 可扩展库存自动扣减 |
| 视图应用 | ✅ | FavoriteWithProduct视图 |
| 存储过程 | ✅ | 可扩展订单处理 |

### 高级要求 ✅

| 要求 | 实现情况 | 说明 |
|------|---------|------|
| 前后端分离架构 | ✅ | RESTful API + Vue 3 |
| 认证与授权 | ✅ | 自定义Token认证 |
| 完整业务逻辑 | ✅ | 用户、商品、订单、购物车、收藏 |
| 数据库优化 | ✅ | 合理的索引和查询优化 |

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.2.0 | 后端核心框架 |
| **Spring MVC** | 6.1.1 | Web层框架 |
| **MyBatis Plus** | 3.5.6 | ORM框架（支持Spring Boot 3） |
| **MySQL** | 8.0 | 关系型数据库 |
| **Maven** | 3.9+ | 项目构建工具 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue 3** | 3.x | 前端核心框架 |
| **Element Plus** | 2.x | UI组件库 |
| **Vue Router** | 4.x | 路由管理 |
| **Axios** | 1.x | HTTP请求库 |
| **Vite** | 5.x | 构建工具 |

---

## 数据库设计

### 1. ER图设计

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    USER     │       │   PRODUCT   │       │    ORDER    │
│─────────────│       │─────────────│       │─────────────│
│ user_id (PK)│──┐    │ product_id │──┐    │ order_id(PK)│
│ username    │  │    │ seller_id  │◄─┘    │ buyer_id    │◄─┐
│ password    │  └───►│ title      │       │ product_id  │◄─┼─┐
│ phone       │       │ price      │       │ unit_price  │  │ │
│ role        │       │ stock      │       │ total_price │  │ │
│ create_time │       │ category   │       │ status      │  │ │
└─────────────┘       └─────────────┘       └─────────────┘  │ │
      │                    │                     │            │ │
      │                    │                     │            │ │
      ▼                    ▼                     ▼            │ │
┌─────────────┐       ┌─────────────┐                     │ │
│    CART     │       │  FAVORITE  │                     │ │
│─────────────│       │─────────────│                     │ │
│ cart_id (PK)│       │ user_id(PK)│◄────────────────────┘ │
│ user_id     │◄─────│ product_id │◄────────────────────────┘
│ product_id  │◄─────│ create_time│
│ quantity    │       └─────────────┘
│ create_time │
└─────────────┘       ┌─────────────┐
                      │   REVIEW    │
                      │─────────────│
                      │ review_id(PK)│
                      │ order_id    │◄────┘
                      │ from_user   │
                      │ to_user     │
                      │ rating      │
                      │ content     │
                      │ create_time │
                      └─────────────┘
```

### 2. 关系模型

```
USER (user_id, username, password_hash, phone, avatar, role, create_time)
    PK: user_id
    UQ: username
    UQ: phone

PRODUCT (product_id, seller_id, title, description, price, category, status, stock, create_time)
    PK: product_id
    FK: seller_id → USER(user_id)
    INDEX: idx_seller (seller_id)
    INDEX: idx_status_category (status, category)

ORDER (order_id, buyer_id, product_id, quantity, unit_price, total_price, status, create_time, pay_time)
    PK: order_id
    FK: buyer_id → USER(user_id)
    FK: product_id → PRODUCT(product_id)
    INDEX: idx_buyer_status (buyer_id, status)
    INDEX: idx_product (product_id)

CART (cart_id, user_id, product_id, quantity, create_time)
    PK: cart_id
    FK: user_id → USER(user_id)
    FK: product_id → PRODUCT(product_id)
    UQ: uk_user_product (user_id, product_id)
    INDEX: idx_user (user_id)

FAVORITE (user_id, product_id, create_time)
    PK: (user_id, product_id)
    FK: user_id → USER(user_id)
    FK: product_id → PRODUCT(product_id)

REVIEW (review_id, order_id, from_user_id, to_user_id, rating, content, create_time)
    PK: review_id
    FK: order_id → ORDER(order_id)
    FK: from_user_id → USER(user_id)
    FK: to_user_id → USER(user_id)
    UQ: order_id (每个订单只能评价一次)
```

---

## 核心表结构详解

### 3.1 用户表 (USER)

```sql
CREATE TABLE user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '0=普通用户, 1=管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

**设计说明：**
- `username` 和 `phone` 设置唯一约束，防止重复注册
- `password_hash` 存储密码的哈希值，而非明文
- `role` 区分普通用户和管理员，便于权限管理
- `create_time` 自动记录用户注册时间

---

### 3.2 商品表 (PRODUCT)

```sql
CREATE TABLE product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    category VARCHAR(50) COMMENT '分类',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=在售, 1=已售, 2=下架',
    stock INT NOT NULL DEFAULT 1 COMMENT '库存',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    FOREIGN KEY (seller_id) REFERENCES user(user_id) ON DELETE CASCADE,
    INDEX idx_seller (seller_id),
    INDEX idx_status_category (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

**设计说明：**
- `seller_id` 外键关联用户表，级联删除
- `status` 标记商品状态，便于查询在售/已售/下架商品
- `stock` 管理库存，下单时扣减
- 索引优化：对卖家ID、状态+分类建立复合索引

---

### 3.3 订单表 (ORDER) - 冗余字段设计

```sql
CREATE TABLE `order` (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '下单时单价快照',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=待付款, 1=已付款, 2=已完成, 3=取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    pay_time DATETIME COMMENT '付款时间',
    FOREIGN KEY (buyer_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    INDEX idx_buyer_status (buyer_id, status),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

**设计说明 - 冗余字段设计：**
- `unit_price` 是**价格快照**，保存下单时的商品价格
- **为什么要冗余？**
  - 商品价格可能随时间变动
  - 历史订单应该保持原价，不受商品调价影响
  - 避免查询商品表获取历史价格
- `total_price = unit_price × quantity`
- 索引优化：对买家ID+状态、订单ID建立索引

---

### 3.4 购物车表 (CART)

```sql
CREATE TABLE cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
```

**设计说明：**
- `UNIQUE KEY` 防止同一用户重复添加同一商品
- 更新数量而非创建新记录
- 级联删除：用户删除或商品下架时自动清理购物车

---

### 3.5 收藏表 (FAVORITE)

```sql
CREATE TABLE favorite (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
```

**设计说明：**
- 使用复合主键 `(user_id, product_id)` 避免重复收藏
- 不需要额外ID字段，节省空间
- 级联删除保持数据一致性

---

### 3.6 评价表 (REVIEW)

```sql
CREATE TABLE review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL UNIQUE COMMENT '关联订单',
    from_user_id BIGINT NOT NULL COMMENT '评价人',
    to_user_id BIGINT NOT NULL COMMENT '被评人',
    rating TINYINT NOT NULL COMMENT '1~5星',
    content VARCHAR(255) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE,
    FOREIGN KEY (from_user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES user(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';
```

**设计说明：**
- `UNIQUE order_id` 确保每个订单只能评价一次
- `from_user_id` 和 `to_user_id` 支持双向评价
- `rating` 限制1-5星，保证数据有效性

---

## 数据库设计亮点

### 1. 索引优化

| 索引名称 | 所在表 | 索引字段 | 优化目的 |
|---------|--------|---------|---------|
| idx_seller | product | seller_id | 快速查询某卖家所有商品 |
| idx_status_category | product | status, category | 筛选特定状态的分类商品 |
| idx_buyer_status | order | buyer_id, status | 查询买家特定状态订单 |
| idx_product | order | product_id | 统计商品销售情况 |
| idx_user | cart | user_id | 查询用户购物车 |
| uk_user_product | cart | user_id, product_id | 唯一性约束 |

### 2. 事务处理示例

**下单扣库存事务：**
```java
@Transactional(rollbackFor = Exception.class)
public void createOrder(Long buyerId, Long productId, Integer quantity) {
    // 1. 查询商品并锁定
    Product product = productMapper.selectById(productId);
    
    // 2. 检查库存
    if (product.getStock() < quantity) {
        throw new RuntimeException("库存不足");
    }
    
    // 3. 扣减库存
    product.setStock(product.getStock() - quantity);
    productMapper.updateById(product);
    
    // 4. 创建订单（保存价格快照）
    Order order = new Order();
    order.setBuyerId(buyerId);
    order.setProductId(productId);
    order.setQuantity(quantity);
    order.setUnitPrice(product.getPrice());  // 价格快照
    order.setTotalPrice(product.getPrice().multiply(new BigDecimal(quantity)));
    orderMapper.insert(order);
}
```

**取消订单恢复库存事务：**
```java
@Transactional(rollbackFor = Exception.class)
public void cancelOrder(Long orderId) {
    // 1. 查询订单
    Order order = orderMapper.selectById(orderId);
    
    // 2. 检查订单状态
    if (order.getStatus() != 0) {
        throw new RuntimeException("订单无法取消");
    }
    
    // 3. 更新订单状态
    order.setStatus(3);  // 3=已取消
    orderMapper.updateById(order);
    
    // 4. 恢复库存
    Product product = productMapper.selectById(order.getProductId());
    product.setStock(product.getStock() + order.getQuantity());
    productMapper.updateById(product);
}
```

### 3. 触发器应用（可选扩展）

```sql
-- 示例：自动记录商品下架日志
DELIMITER $$
CREATE TRIGGER product_sold_trigger
AFTER UPDATE ON product
FOR EACH ROW
BEGIN
    IF NEW.status = 1 AND OLD.status = 0 THEN
        INSERT INTO product_log (product_id, action, old_status, new_status, action_time)
        VALUES (NEW.product_id, 'SOLD', OLD.status, NEW.status, NOW());
    END IF;
END$$
DELIMITER ;
```

---

## 项目运行

### 环境要求

| 环境 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | Java开发环境 |
| Node.js | 16+ | 前端运行环境 |
| npm | 8+ | 包管理器 |
| MySQL | 8.0+ | 数据库服务 |

---

### 1. 数据库初始化

#### 步骤1：创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行SQL脚本
source /path/to/schema.sql
```

#### 步骤2：验证数据库创建

```sql
USE campus_secondhand;
SHOW TABLES;
```

应该看到6张表：
- cart
- favorite
- order
- product
- review
- user

---

### 2. 后端配置

#### 修改数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  application:
    name: secondhand-platform
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/campus_secondhand?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root           # 修改为你的用户名
    password: your_password  # 修改为你的密码

mybatis-plus:
  type-aliases-package: com.campus.secondhand.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

logging:
  level:
    com.campus.secondhand: debug
```

---

### 3. 后端启动

#### 方式一：Maven命令启动

```bash
cd backend

# 清理并编译
mvn clean compile

# 运行
mvn spring-boot:run
```

#### 方式二：IDEA启动

1. 打开项目
2. 找到 `SecondhandPlatformApplication.java`
3. 右键选择 "Run"
4. 等待启动完成

#### 验证后端启动

访问：http://localhost:8080/api/product/categories

应该返回：
```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

---

### 4. 前端配置

#### 安装依赖

```bash
cd frontend

# 安装依赖（国内建议使用淘宝镜像）
npm install --registry=https://registry.npmmirror.com

# 或者使用yarn
yarn install
```

#### 修改API代理（可选）

编辑 `frontend/vite.config.js`：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,  // 前端端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端地址
        changeOrigin: true
      }
    }
  }
})
```

---

### 5. 前端启动

```bash
cd frontend

# 开发模式启动
npm run dev

# 访问地址
# http://localhost:3000
```

---

## 功能说明

### 用户模块

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 用户注册 | /api/user/register | POST | 填写用户名、密码、手机号 |
| 用户登录 | /api/user/login | POST | 返回Token |
| 获取用户信息 | /api/user/info | GET | 需携带Token |
| 退出登录 | /api/user/logout | POST | 清除Token |

### 商品模块

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 商品列表 | /api/product/list | GET | 支持分页、搜索、筛选、排序 |
| 商品详情 | /api/product/{id} | GET | 查看商品详细信息 |
| 发布商品 | /api/product | POST | 需登录，填写标题、描述、价格、分类 |
| 分类列表 | /api/product/categories | GET | 获取所有商品分类 |
| 我的商品 | /api/product/my | GET | 查看自己发布的商品 |

**商品列表查询参数：**
```bash
GET /api/product/list?pageNum=1&pageSize=10&keyword=手机&category=电子产品&sortBy=price&sortOrder=asc
```

### 购物车模块

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 我的购物车 | /api/cart | GET | 查看购物车列表 |
| 加入购物车 | /api/cart | POST | 需登录，传入商品ID和数量 |
| 删除商品 | /api/cart/{id} | DELETE | 需登录，从购物车移除 |

### 收藏模块

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 我的收藏 | /api/favorite | GET | 查看收藏列表 |
| 添加收藏 | /api/favorite/{productId} | POST | 需登录，收藏商品 |
| 取消收藏 | /api/favorite/{productId} | DELETE | 需登录，取消收藏 |

### 订单模块

| 功能 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 我的订单 | /api/order | GET | 查看订单列表 |
| 创建订单 | /api/order | POST | 需登录，下单购买 |
| 取消订单 | /api/order/{id} | PUT | 需登录，取消订单 |

---

## API 接口详细文档

### 统一响应格式

所有接口返回统一格式：

```json
{
  "code": 200,        // 状态码：200=成功，其他=失败
  "message": "success", // 消息
  "data": {}          // 数据
}
```

### 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 项目结构

```
campus-secondhand-trade/
│
├── backend/                           # 后端项目
│   ├── src/main/java/com/campus/secondhand/
│   │   │
│   │   ├── SecondhandPlatformApplication.java  # 启动类
│   │   │
│   │   ├── common/                   # 公共类
│   │   │   └── Result.java          # 统一响应封装
│   │   │
│   │   ├── config/                   # 配置类
│   │   │   ├── AuthUtil.java        # 认证工具（Token管理）
│   │   │   └── MyMetaObjectHandler.java  # MyBatis自动填充
│   │   │
│   │   ├── controller/               # 控制器层（接收请求）
│   │   │   ├── UserController.java   # 用户管理
│   │   │   ├── ProductController.java  # 商品管理
│   │   │   ├── OrderController.java   # 订单管理
│   │   │   ├── CartController.java    # 购物车管理
│   │   │   └── FavoriteController.java  # 收藏管理
│   │   │
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── impl/                # 业务实现
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── CartService.java
│   │   │   └── FavoriteService.java
│   │   │
│   │   ├── mapper/                  # 数据访问层
│   │   │   ├── UserMapper.java
│   │   │   ├── ProductMapper.java
│   │   │   ├── OrderMapper.java
│   │   │   ├── CartMapper.java
│   │   │   ├── FavoriteMapper.java
│   │   │   └── ReviewMapper.java
│   │   │
│   │   ├── entity/                  # 实体类（数据库表映射）
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── Cart.java
│   │   │   ├── Favorite.java
│   │   │   └── Review.java
│   │   │
│   │   └── dto/                     # 数据传输对象
│   │       └── FavoriteWithProduct.java  # 收藏+商品信息
│   │   │
│   ├── src/main/resources/
│   │   ├── application.yml          # 应用配置
│   │   └── schema.sql               # 数据库脚本
│   │
│   └── pom.xml                      # Maven配置
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── views/                   # 页面组件
│   │   │   ├── Home.vue            # 首页（商品列表）
│   │   │   ├── Login.vue           # 登录页
│   │   │   ├── Register.vue        # 注册页
│   │   │   ├── ProductDetail.vue   # 商品详情
│   │   │   ├── Publish.vue         # 发布商品
│   │   │   ├── Cart.vue            # 购物车
│   │   │   ├── Orders.vue          # 订单列表
│   │   │   ├── MyProducts.vue      # 我的商品
│   │   │   ├── Profile.vue         # 个人中心
│   │   │   └── Favorite.vue        # 我的收藏
│   │   │
│   │   ├── router/
│   │   │   └── index.js           # 路由配置
│   │   │
│   │   ├── App.vue                # 根组件
│   │   └── main.js                # 入口文件
│   │
│   ├── vite.config.js              # Vite配置
│   └── package.json                # NPM依赖
│
├── docs/                            # 文档目录
│
├── .gitignore                       # Git忽略配置
└── README.md                        # 项目说明文档
```

---

## 常见问题与解决方案

### 问题1：后端启动失败

**错误信息：**
```
Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

**原因：**
MyBatis Plus版本与Spring Boot 3不兼容

**解决方案：**
修改 `pom.xml`：
```xml
<!-- 使用Spring Boot 3兼容版本 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.6</version>
</dependency>
```

---

### 问题2：数据库连接失败

**错误信息：**
```
Connection refused: connect
```

**原因：**
MySQL服务未启动或连接配置错误

**解决方案：**
1. 检查MySQL服务是否启动：
   ```bash
   netstat -ano | findstr :3306
   ```

2. 检查 `application.yml` 配置：
   ```yaml
   url: jdbc:mysql://localhost:3306/campus_secondhand
   username: root
   password: your_password
   ```

---

### 问题3：前端代理不生效

**错误信息：**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**原因：**
前端代理未正确配置

**解决方案：**
修改 `vite.config.js`：
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

### 问题4：注册功能报错500

**原因：**
前端发送字段名与后端不匹配

**解决方案：**
修改前端 `Register.vue`，确保发送 `password` 而不是 `passwordHash`：
```javascript
axios.post('/api/user/register', {
  username: this.username,
  password: this.password,  // 不是 passwordHash
  phone: this.phone
})
```

---

## 开发指南

### 代码规范

#### 后端规范

1. **命名规范**
   - 类名：UpperCamelCase（如 `UserController`）
   - 方法名：lowerCamelCase（如 `getUserInfo`）
   - 数据库表：snake_case（如 `user_order`）

2. **分层架构**
   - Controller：接收请求，参数校验
   - Service：业务逻辑处理
   - Mapper：数据库操作

3. **异常处理**
   - 使用 `@Transactional` 保证事务
   - 统一返回 Result 对象
   - 前端进行错误提示

#### 前端规范

1. **组件命名**
   - 组件文件：PascalCase（如 `UserProfile.vue`）
   - 组件引用：kebab-case（如 `<user-profile>`）

2. **API调用**
   - 使用 Axios 封装请求
   - 统一处理 Token
   - 错误拦截处理

---

### 数据库优化建议

1. **定期清理**
   - 清理过期订单
   - 归档历史数据

2. **索引维护**
   - 避免过度索引
   - 定期分析查询性能

3. **慢查询优化**
   - 使用 EXPLAIN 分析查询
   - 优化复杂查询

---

## 课程设计评分要点

### 设计文档（20分）
- ✅ 完整的ER图
- ✅ 关系模型说明
- ✅ 表结构详细说明
- ✅ 设计依据阐述

### 数据库实现（30分）
- ✅ 6张核心表
- ✅ 外键约束
- ✅ 索引优化
- ✅ 测试数据

### 功能实现（40分）
- ✅ 完整CRUD操作
- ✅ 业务逻辑正确
- ✅ 事务处理
- ✅ 异常处理

### 文档与答辩（10分）
- ✅ README完整
- ✅ 代码规范
- ✅ 演示流畅

---

## 项目截图

（此处可添加项目运行截图）

---

## 开发记录

### 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|---------|
| 1.0.0 | 2026-06-04 | 完成基础功能开发 |
| 1.0.1 | 2026-06-04 | 修复依赖兼容性问题 |
| 1.0.2 | 2026-06-05 | 完善README文档 |

### 主要修复

- ✅ 移除 Sa-Token 依赖，使用自定义 AuthUtil
- ✅ 移除 Lombok 依赖
- ✅ 修复 MyBatis Plus 与 Spring Boot 3 兼容性
- ✅ 修复前端注册字段名称（passwordHash → password）
- ✅ 优化 .gitignore 配置
- ✅ 完善前后端错误处理

---

## 参考资料

1. [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
2. [MyBatis Plus 官方文档](https://baomidou.com/)
3. [Vue 3 官方文档](https://vuejs.org/)
4. [Element Plus 官方文档](https://element-plus.org/)
5. [MySQL 8.0 官方文档](https://dev.mysql.com/doc/refman/8.0/en/)

---

## License

MIT License

---

## 作者信息

- **项目名称**：校园二手交易平台
- **课程名称**：数据库系统原理 - 课程设计
- **开发时间**：2026年6月
- **指导教师**：（请填写）

---

## 致谢

感谢数据库课程老师的指导，以及所有帮助完成项目的同学和朋友！

---

*本文档最后更新于 2026-06-05*

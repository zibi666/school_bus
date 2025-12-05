# School Bus Backend

基于 Spring Boot 开发的校园包车系统后端服务。

## 🛠 技术栈

- **核心框架**: Spring Boot 3.x
- **持久层**: Spring Data JPA
- **数据库**: MySQL 8.0
- **工具库**: Lombok

## 🚀 快速开始

### 1. 环境准备

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 找到项目根目录下的 `database/school_bus.sql` 文件。
2. 在 MySQL 中执行该 SQL 脚本，创建数据库 `school_bus` 及相关表结构 (`admin_info`, `student_info`, `t_bus`, `t_order`)。

### 3. 修改配置

打开 `src/main/resources/application.properties`，根据你的本地环境修改数据库连接信息：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/school_bus?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 4. 启动项目

运行 `src/main/java/com/lm/school_bus/SchoolBusApplication.java` 中的 `main` 方法启动服务。

服务默认运行在端口 `8080`。

## 📚 接口文档

详细的接口测试指南请参考同目录下的 [APIFOX_TEST_GUIDE.md](./APIFOX_TEST_GUIDE.md)。

主要模块包括：
- **Auth**: 登录注册
- **Student**: 个人信息、包车申请、订单管理
- **Admin**: 订单审核、车辆管理

## 📂 目录结构

```
com.lm.school_bus
├── controller  // 控制层 (API 接口)
├── service     // 业务逻辑层
├── repository  // 数据访问层 (JPA)
├── entity      // 数据库实体
└── dto         // 数据传输对象
```

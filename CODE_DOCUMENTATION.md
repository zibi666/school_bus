# 校车预约系统 - 代码文档

## 项目概述

**校车预约系统** 是一个基于 Spring Boot 和 Vue 3 的全栈应用，支持学生在线预约校车、同学之间拼车分享等功能。

### 核心特性

1. **学生账号管理**：注册/登录
2. **订单管理**：创建订单、支付、查看订单、退票
3. **邀请码系统**：生成邀请码、邀请同学加入订单、实现拼车功能
4. **级联退票**：申请人退票时，同邀请码下所有订单都变为"已退票"
5. **管理员功能**：审核订单、分配车辆、管理车辆信息、验证时间冲突

---

## 技术栈

### 后端

- **框架**：Spring Boot 2.x
- **ORM**：MyBatis（XML 方式）
- **数据库**：MySQL 5.7+
- **工具库**：Lombok、jackson

### 前端

- **框架**：Vue 3
- **构建工具**：Vite
- **UI 库**：Element Plus
- **HTTP 客户端**：Axios

---

## 后端架构

### 项目结构

```
src/main/java/com/lm/school_bus/
├── controller/          # 控制层（REST API）
│   ├── AuthController.java       # 认证接口
│   ├── StudentController.java     # 学生业务接口
│   └── AdminController.java       # 管理员业务接口
├── service/            # 业务逻辑层
│   ├── AuthService.java          # 认证业务
│   ├── StudentService.java        # 学生业务
│   └── AdminService.java          # 管理员业务
├── mapper/             # 数据访问层
│   ├── StudentMapper.java
│   ├── AdminMapper.java
│   ├── BusMapper.java
│   └── OrderMapper.java
├── entity/             # 数据实体
│   ├── Student.java
│   ├── Admin.java
│   ├── Bus.java
│   └── Order.java
├── dto/                # 数据传输对象
│   └── ApiResponse.java
├── exception/          # 异常定义
│   └── BusinessException.java
├── config/             # 配置类
│   └── GlobalExceptionHandler.java
├── util/               # 工具类
│   └── PriceCalculator.java
└── SchoolBusApplication.java    # 应用入口
```

### 代码注释说明

#### 1. 控制层（Controllers）

**AuthController.java**
- `studentLogin()`: 学生登录
- `studentRegister()`: 学生注册
- `adminLogin()`: 管理员登录

**StudentController.java**（11 个端点）
- `createOrder()`: POST `/student/order` - 创建新订单
- `getMyOrders()`: GET `/student/orders/{studentId}` - 获取我的订单
- `payOrder()`: POST `/student/order/pay/{orderId}` - 支付订单
- `calculatePrice()`: POST `/student/order/calculate-price` - 计算价格
- `getOrderByCode()`: GET `/student/order/by-invitation-code/{code}` - 通过邀请码查询原始订单
- `joinByCode()`: POST `/student/order/join-by-invitation-code/{code}` - 加入拼车订单
- `refund()`: POST `/student/order/refund/{orderId}` - 申请退票（级联退票）
- `leave()`: POST `/student/order/leave/{orderId}` - 离开订单（仅加入者）
- `getProfile()`: GET `/student/profile/{studentId}` - 获取个人信息
- `updateProfile()`: PUT `/student/profile/{studentId}` - 更新个人信息
- `getBus()`: GET `/student/bus/{busId}` - 获取车辆详情

**AdminController.java**（7 个端点）
- `getAllOrders()`: GET `/admin/orders` - 获取所有订单
- `approveOrder()`: POST `/admin/order/approve` - 审核通过
- `rejectOrder()`: POST `/admin/order/reject` - 审核拒绝
- `revokeOrder()`: POST `/admin/order/revoke` - 撤销订单
- `getAllBuses()`: GET `/admin/buses` - 获取车辆列表
- `addBus()`: POST `/admin/bus` - 添加车辆
- `deleteBus()`: DELETE `/admin/bus/{busId}` - 删除车辆

#### 2. 业务逻辑层（Services）

**AuthService.java**
- 学生登录验证
- 学生注册
- 管理员登录验证

**StudentService.java**（核心业务，包含邀请码系统）
- `createOrder()`: 创建订单，自动生成 8 位邀请码
- `getOrderByInvitationCode()`: 查询邀请码对应的原始订单
- `joinOrderByInvitationCode()`: 通过邀请码加入订单，创建新订单记录
- `refundOrder()`: 批量更新同邀请码下所有订单为"已退票"（级联）
- `leaveJoinedOrder()`: 加入者离开订单，物理删除记录
- `calculateOrderPrice()`: 根据时间和车型计算价格

**AdminService.java**
- `getAllOrders()`: 获取所有已支付订单
- `approveOrder()`: 审核通过，分配车辆，检查时间冲突
- `rejectOrder()`: 审核拒绝，记录原因
- `revokeOrder()`: 撤销已通过订单
- `addBus()`: 添加车辆，自动设置价格
- `deleteBus()`: 删除车辆，级联更新相关订单

#### 3. 数据访问层（Mappers）

**OrderMapper.java**（核心数据操作）
- `selectByInvitationCodeAll()`: 查询邀请码下所有订单
- `selectExistingJoinedOrder()`: 检查是否已加入
- `updateStatusByInvitationCode()`: 批量更新邀请码订单状态
- `checkTimeConflict()`: 检查时间冲突

**StudentMapper.java**
- CRUD 操作
- `countById()`: 计数，用于重复性检查

**BusMapper.java**
- CRUD 操作
- `selectByCarType()`: 按车型查询

**AdminMapper.java**
- `selectByAccount()`: 按账号查询管理员

#### 4. 实体类（Entities）

**Order.java**（核心实体，包含拼车逻辑字段）
- `orderId`: 订单 ID
- `studentId`: 申请人学号
- `destination`: 目的地
- `requestedCarType`: 需求车型
- `busId`: 分配的车辆 ID
- `price`: 订单费用
- `status`: 状态（审核中/已通过/已拒绝/已退票）
- `invitationCode`: 邀请码（8 位，格式如 ABC12345）
- `startTime/endTime`: 使用时间段
- `isApplicant`: 是否申请人（1=申请人，0=加入者）
- `bus`: 关联的车辆对象

**Student.java**
- `studentId`: 学号
- `name`: 姓名
- `password`: 密码
- `location`: 所在地

**Bus.java**
- `busId`: 车辆 ID
- `plateNumber`: 车牌号
- `carType`: 车型（45座大巴/中巴/商务车）
- `driverName`: 司机姓名
- `number`: 司机号码（电话）
- `price`: 单价（每小时费用）

**Admin.java**
- `adminId`: 管理员 ID
- `account`: 账号
- `password`: 密码
- `name`: 真实姓名

#### 5. 数据传输对象（DTOs）

**ApiResponse.java**（统一响应格式）
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```
- 方法：`success(data)`, `error(code, message)`

#### 6. 异常处理

**BusinessException.java**
- 自定义业务异常，包含错误码和消息
- 错误码映射：
  - 400: 请求错误、业务规则违反
  - 401: 身份认证失败（如密码错误）
  - 403: 权限不足（如非申请人无法退票）
  - 404: 资源不存在
  - 500: 服务器内部错误

**GlobalExceptionHandler.java**
- 全局异常处理
- 捕获 BusinessException，根据 code 映射到 HTTP 状态码
- 捕获其他异常，统一返回 500

#### 7. 工具类

**PriceCalculator.java**
- `calculateHours()`: 根据开始和结束时间计算小时数
- `calculatePrice()`: 计算订单价格
- `formatHours()`: 格式化小时为可读文本（如"5小时30分钟"）
- `formatTimeRange()`: 格式化时间范围（如"12月10日 15:00-20:00"）

---

## 前端架构

### 项目结构

```
src/
├── api/
│   └── index.js           # API 请求模块（所有后端 API 调用）
├── router/
│   └── index.js           # Vue Router 路由配置
├── views/
│   ├── Login.vue          # 登录页面
│   ├── Register.vue       # 注册页面
│   ├── student/
│   │   ├── Layout.vue     # 学生布局
│   │   ├── Charter.vue    # 包车申请
│   │   ├── MyTrips.vue    # 我的行程
│   │   └── Profile.vue    # 个人资料
│   └── admin/
│       ├── Layout.vue     # 管理员布局
│       ├── Trips.vue      # 订单管理
│       └── Drivers.vue    # 车辆管理
├── components/            # 通用组件
├── assets/                # 静态资源
├── App.vue                # 根组件
└── main.js                # 应用入口
```

### 代码注释说明

#### 1. main.js
- Vue 3 应用初始化
- 注册 Vue Router 和 Element Plus

#### 2. router/index.js
- 路由配置（6 个主路由）
- 路由分层：公开（登录/注册）、学生端（3 个子路由）、管理员端（2 个子路由）
- 使用动态导入（懒加载）优化性能

#### 3. api/index.js
- Axios 实例创建和配置
- 响应拦截器，统一处理 API 响应格式
- 导出 24+ 个 API 函数，分组为：
  - 认证（3 个）
  - 学生业务（11 个）
  - 管理员业务（8 个）

#### 4. 页面组件

**Login.vue**
- 学生/管理员登录表单
- 校验表单数据
- 调用 `loginStudent()` 或 `loginAdmin()` API

**Register.vue**
- 学生注册表单
- 创建账号

**Student - Charter.vue**
- 提交包车申请
- 选择目的地、时间、车型
- 计算价格（实时调用 `calculateOrderPrice()` API）

**Student - MyTrips.vue**
- 显示学生的所有订单
- 支持的操作：
  - 查看订单详情
  - 支付订单（`payOrder()`）
  - 通过邀请码加入拼车（`getOrderByInvitationCode()` + `joinOrderByInvitationCode()`）
  - 申请退票（`refundOrder()`）
  - 离开订单（`leaveOrder()`）

**Student - Profile.vue**
- 显示和修改个人信息
- 修改姓名、位置、密码

**Admin - Trips.vue**
- 显示所有订单（已支付的）
- 支持的操作：
  - 审核通过（`approveOrder()`）
  - 审核拒绝（`rejectOrder()`）
  - 撤销订单（`revokeOrder()`）

**Admin - Drivers.vue**
- 显示所有车辆
- 支持的操作：
  - 添加车辆（`addBus()`）
  - 编辑车辆
  - 删除车辆（`deleteBus()`）
  - 检查可用性（`checkBusAvailability()`）

---

## 数据库设计

### 表结构

#### 1. admin_info（管理员表）
| 字段 | 类型 | 说明 |
|------|------|------|
| admin_id | INT | 管理员 ID（主键） |
| account | VARCHAR(255) | 账号（唯一） |
| password | VARCHAR(255) | 密码 |
| name | VARCHAR(255) | 真实姓名 |

#### 2. student_info（学生表）
| 字段 | 类型 | 说明 |
|------|------|------|
| student_id | VARCHAR(20) | 学号（主键） |
| name | VARCHAR(255) | 学生姓名 |
| password | VARCHAR(255) | 密码 |
| location | VARCHAR(255) | 所在地 |

#### 3. t_bus（车辆表）
| 字段 | 类型 | 说明 |
|------|------|------|
| bus_id | INT | 车辆 ID（主键） |
| plate_number | VARCHAR(255) | 车牌号（唯一） |
| car_type | VARCHAR(255) | 车型 |
| driver_name | VARCHAR(255) | 司机姓名 |
| price | INT | 单价（每小时） |
| number | VARCHAR(255) | 司机号码 |

#### 4. t_order（订单表）
| 字段 | 类型 | 说明 |
|------|------|------|
| order_id | INT | 订单 ID（主键） |
| student_id | VARCHAR(20) | 申请人学号（外键） |
| destination | VARCHAR(255) | 目的地 |
| requested_car_type | VARCHAR(255) | 需求车型 |
| bus_id | INT | 分配的车辆 ID（外键） |
| price | DECIMAL(10,2) | 订单费用 |
| status | VARCHAR(20) | 状态 |
| reject_reason | VARCHAR(255) | 拒绝原因 |
| is_paid | TINYINT(1) | 是否已支付 |
| invitation_code | VARCHAR(20) | 邀请码 |
| start_time | DATETIME | 使用开始时间 |
| end_time | DATETIME | 使用结束时间 |
| is_applicant | TINYINT(4) | 是否申请人 |

**索引设计**
- `idx_order_bus_time`: (bus_id, start_time, end_time, status) - 用于检查时间冲突
- `idx_invitation_code_status`: (invitation_code, status) - 用于邀请码查询

---

## 核心业务逻辑

### 邀请码系统（拼车）

#### 工作流程

1. **创建订单**
   - 学生提交包车申请
   - 系统自动生成 8 位邀请码（字符范围 A-Z0-9）
   - 创建 Order 记录，`isApplicant = 1`

2. **加入拼车**
   - 其他学生输入邀请码
   - 系统查询邀请码对应的原始订单
   - 创建新的 Order 记录，同邀请码，`isApplicant = 0`
   - 复制原订单的 destination、carType、busId、price 等字段

3. **申请人退票**（级联）
   - 申请人（isApplicant = 1）申请退票
   - 系统查询所有同邀请码的订单
   - 将所有订单状态更新为"已退票"
   - 同邀请码的加入者（isApplicant = 0）也被标记为"已退票"

4. **加入者离开**
   - 加入者（isApplicant = 0）可以离开订单
   - 物理删除该 Order 记录
   - 申请人订单不受影响

#### 权限模型

| 操作 | 申请人 | 加入者 |
|------|--------|--------|
| 查看邀请码 | ✓ | ✗ |
| 申请退票 | ✓ | ✗ |
| 离开订单 | ✗ | ✓ |
| 查看订单 | ✓ | ✓ |

### 订单审核流程

1. **学生创建订单**
   - 状态：`审核中`
   - is_paid：false（等待支付）

2. **学生支付订单**
   - 前提：status = `审核中`，is_paid = false
   - 支付后：is_paid = true
   - 通知管理员审核

3. **管理员审核**
   - **通过**：
     - 检查是否有时间冲突
     - 分配 busId
     - status → `已通过`
   - **拒绝**：
     - 记录拒绝原因
     - status → `已拒绝`

4. **撤销订单**
   - 仅能撤销 status = `已通过` 的订单
   - status → `已拒绝`
   - 释放 busId

### 时间冲突检查

管理员分配车辆前，系统会检查该车辆在给定时间段内是否有已通过的订单。

**SQL 逻辑**
```sql
-- 检查时间重叠的已通过订单
SELECT COUNT(*)
FROM t_order
WHERE bus_id = ?
  AND status = '已通过'
  AND (
    -- 时间范围重叠：
    -- 新订单的开始时间 < 现有订单的结束时间
    -- 新订单的结束时间 > 现有订单的开始时间
    start_time < ? AND end_time > ?
  )
```

---

## 错误处理

### 业务异常映射

| 错误码 | HTTP 状态 | 常见场景 |
|--------|-----------|---------|
| 400 | Bad Request | 订单状态错误、参数不合法、重复加入 |
| 401 | Unauthorized | 密码错误、账号不存在 |
| 403 | Forbidden | 权限不足（如非申请人无权退票） |
| 404 | Not Found | 订单不存在、学生不存在 |
| 500 | Internal Error | 数据库异常、网络异常等 |

### 响应格式

**成功响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

**错误响应**
```json
{
  "code": 400,
  "message": "订单状态错误，无法支付",
  "data": null
}
```

---

## 部署说明

### 后端部署

1. **环境要求**
   - JDK 8+
   - MySQL 5.7+
   - Maven 3.6+

2. **配置**
   - 修改 `application.properties` 中的数据库连接信息
   - 执行 `school_bus.sql` 初始化数据库

3. **构建和运行**
   ```bash
   mvn clean package
   java -jar school_bus-0.0.1-SNAPSHOT.jar
   ```

### 前端部署

1. **环境要求**
   - Node.js 14+
   - npm 或 yarn

2. **安装依赖**
   ```bash
   npm install
   ```

3. **开发模式**
   ```bash
   npm run dev
   ```

4. **生产构建**
   ```bash
   npm run build
   ```

---

## 常见问题

### Q: 邀请码是否唯一？
A: 邀请码是唯一的。系统为每个订单生成不同的邀请码，支持多个学生使用同一邀请码加入该订单。

### Q: 加入者支付订单吗？
A: 不需要。加入者的订单是复制申请人的订单信息创建的，与申请人共享订单费用（无需额外支付）。

### Q: 申请人退票后，加入者的订单会怎样？
A: 加入者的订单状态会变为"已退票"（级联更新），不能进行任何操作。

### Q: 如何防止学生重复加入同一订单？
A: 系统会检查该学生是否已经加入过该邀请码，如已加入则返回错误。

---

## 代码注释总结

本项目所有主要代码文件都已添加了详细的注释，包括：

- **Java 类**：类级别的 JavaDoc、方法级别的 @param/@return/@throws 标签、业务逻辑的内部注释
- **前端文件**：模块级别的注释、API 函数的参数和返回值说明、路由的功能描述
- **SQL 文件**：表字段的注释、索引的说明

通过这些注释，开发人员可以快速了解代码的意图和实现细节，降低维护成本。

---

*最后更新：2025年12月12日*

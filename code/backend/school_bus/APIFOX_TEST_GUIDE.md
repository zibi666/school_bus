# ApiFox 测试指南

本指南将帮助你使用 ApiFox 测试 School Bus 后端接口。

## 1. 环境配置

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`

## 2. 接口列表与测试用例

### 2.1 认证模块 (Auth)

#### 学生登录
- **Method**: `POST`
- **URL**: `/api/auth/student/login`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2021001",
    "password": "password123"
  }
  ```

#### 学生注册
- **Method**: `POST`
- **URL**: `/api/auth/student/register`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2021001",
    "name": "张三",
    "password": "password123",
    "location": "南校区"
  }
  ```

#### 管理员登录
- **Method**: `POST`
- **URL**: `/api/auth/admin/login`
- **Body (JSON)**:
  ```json
  {
    "account": "admin",
    "password": "adminpassword"
  }
  ```

---

### 2.2 学生模块 (Student)

#### 获取个人信息
- **Method**: `GET`
- **URL**: `/api/student/profile/{studentId}`
- **Example**: `/api/student/profile/2021001`

#### 提交包车申请
- **Method**: `POST`
- **URL**: `/api/student/order`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2021001",
    "destination": "市中心博物馆",
    "usageTime": "2023-12-25 09:00-17:00",
    "requestedCarType": "大巴 (45座)"
  }
  ```

#### 获取我的订单
- **Method**: `GET`
- **URL**: `/api/student/orders/{studentId}`
- **Example**: `/api/student/orders/2021001`

#### 取消订单
- **Method**: `POST`
- **URL**: `/api/student/order/cancel/{orderId}`
- **Example**: `/api/student/order/cancel/1`

---

### 2.3 管理员模块 (Admin)

#### 获取所有订单
- **Method**: `GET`
- **URL**: `/api/admin/orders`

#### 审批订单 (通过)
- **Method**: `POST`
- **URL**: `/api/admin/order/approve`
- **Body (JSON)**:
  ```json
  {
    "orderId": 1,
    "busId": 101
  }
  ```
  > 注意：`busId` 必须是系统中存在的车辆ID，且该车辆必须处于空闲状态。

#### 拒绝订单
- **Method**: `POST`
- **URL**: `/api/admin/order/reject`
- **Body (JSON)**:
  ```json
  {
    "orderId": 1,
    "reason": "车辆调度紧张，无法安排"
  }
  ```

#### 获取所有车辆
- **Method**: `GET`
- **URL**: `/api/admin/buses`

#### 添加车辆
- **Method**: `POST`
- **URL**: `/api/admin/bus`
- **Body (JSON)**:
  ```json
  {
    "plateNumber": "苏A88888",
    "carType": "大巴 (45座)",
    "driverName": "李师傅",
    "isActive": true
  }
  ```

#### 删除车辆
- **Method**: `DELETE`
- **URL**: `/api/admin/bus/{busId}`
- **Example**: `/api/admin/bus/101`

## 3. 测试流程建议

1. 先调用 **学生注册** 接口创建一个学生账号。
2. 调用 **学生登录** 确认账号可用。
3. 使用该学生账号调用 **提交包车申请** 创建一个订单。
4. 调用 **管理员登录** (需确保数据库中有管理员账号，或手动插入一条)。
5. 管理员调用 **获取所有车辆** 查看可用车辆，若无车辆可先 **添加车辆**。
6. 管理员调用 **获取所有订单** 查看刚才创建的订单。
7. 管理员调用 **审批订单**，传入订单ID和车辆ID。
8. 学生调用 **获取我的订单** 查看订单状态是否变为 "已通过"。

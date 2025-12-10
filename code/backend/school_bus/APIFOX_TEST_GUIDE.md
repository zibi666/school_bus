# ApiFox 测试指南

本指南将帮助你使用 ApiFox 测试 School Bus 后端接口。

## 1. 环境配置

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`

------

## 2. 接口列表与测试用例

### 2.1 认证模块 (Auth)

#### 学生注册
- **Method**: `POST`
- **URL**: `/api/auth/student/register`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2023001",
    "name": "张三",
    "password": "password123",
    "location": "南校区"
  }
  ```

#### 学生登录
- **Method**: `POST`
- **URL**: `/api/auth/student/login`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2023001",
    "password": "password123"
  }
  ```

#### 管理员登录
- **Method**: `POST`
- **URL**: `/api/auth/admin/login`
- **Body (JSON)**:
  ```json
  {
    "account": "admin",
    "password": "123456"
  }
  ```

---

### 2.2 学生模块 (Student)

#### 获取个人信息
- **Method**: `GET`
- **URL**: `/api/student/profile/{studentId}`
- **Example**: `/api/student/profile/2023001`

#### 修改个人信息
- **Method**: `PUT`
- **URL**: `/api/student/profile/{studentId}`
- **Body (JSON)**:
  ```json
  {
    "name": "张三新",
    "location": "北校区",
    "password": "newpassword123"
  }
  ```

#### 提交包车申请
- **Method**: `POST`
- **URL**: `/api/student/order`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2023001",
    "destination": "市中心博物馆",
    "startTime": "2023-12-25 09:00:00",
    "endTime": "2023-12-25 17:00:00",
    "requestedCarType": "45座大巴"
  }
  ```

#### 获取我的订单
- **Method**: `GET`
- **URL**: `/api/student/orders/{studentId}`
- **Example**: `/api/student/orders/2023001`

#### 取消订单（仅审核中状态）
- **Method**: `DELETE`
- **URL**: `/api/student/order/{orderId}`
- **Example**: `/api/student/order/1`

#### 获取车辆详情
- **Method**: `GET`
- **URL**: `/api/student/bus/{busId}`
- **Example**: `/api/student/bus/1`

#### 通过邀请码查询订单
- **Method**: `GET`
- **URL**: `/api/student/order/byInvitationCode/{code}`
- **Example**: `/api/student/order/byInvitationCode/ABC12345`

#### 通过邀请码加入订单
- **Method**: `POST`
- **URL**: `/api/student/order/join`
- **Body (JSON)**:
  ```json
  {
    "studentId": "2023002",
    "invitationCode": "ABC12345"
  }
  ```

#### 申请退票（仅申请人且已通过的订单）
- **Method**: `POST`
- **URL**: `/api/student/order/refund/{orderId}`
- **Params**: `?studentId=2023001`
- **Example**: `/api/student/order/refund/1?studentId=2023001`

---

### 2.3 管理员模块 (Admin)

#### 获取所有订单
- **Method**: `GET`
- **URL**: `/api/admin/orders`
- **Optional Params**: `?status=审核中`

#### 审批订单 (通过)
- **Method**: `POST`
- **URL**: `/api/admin/order/approve`
- **Body (JSON)**:
  ```json
  {
    "orderId": 1,
    "busId": 1
  }
  ```
  > 注意：`busId` 必须是系统中存在的车辆ID，且该车辆在指定时间段内必须没有其他"已通过"的订单。

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

#### 撤销订单（仅已通过的订单）
- **Method**: `POST`
- **URL**: `/api/admin/order/revoke`
- **Body (JSON)**:
  ```json
  {
    "orderId": 1,
    "reason": "管理员主动撤销"
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
    "carType": "45座大巴",
    "driverName": "李师傅",
    "price": 1200,
    "number": "13800138000"
  }
  ```

#### 编辑车辆
- **Method**: `PUT`
- **URL**: `/api/admin/bus/{busId}`
- **Body (JSON)**:
  ```json
  {
    "plateNumber": "苏A88888",
    "carType": "45座大巴",
    "driverName": "李师傅新",
    "price": 1500,
    "number": "13800138001"
  }
  ```

#### 删除车辆
- **Method**: `DELETE`
- **URL**: `/api/admin/bus/{busId}`
- **Example**: `/api/admin/bus/1`
- **说明**: 删除车辆时，所有该车辆的订单状态会自动变为"已拒绝"，拒绝原因为"车辆已删除"

---

## 3. 测试流程建议

### 场景一：基础订单流程

1. **学生注册**：调用学生注册接口创建学生账号
2. **学生登录**：确认账号可用
3. **提交包车申请**：学生创建一个订单（状态：审核中）
4. **管理员登录**：验证管理员账号
5. **添加车辆**：管理员先添加一些车辆（如果系统中没有）
6. **查看订单**：管理员调用"获取所有订单"查看刚创建的订单
7. **审批订单**：管理员调用"审批订单"，传入订单ID和车辆ID，订单状态变为"已通过"
8. **查看订单**：学生调用"获取我的订单"查看订单状态是否变为"已通过"

### 场景二：邀请码加入流程

1. **学生A创建订单**：生成一个邀请码（如：ABC12345）
2. **学生A查看邀请码**：在"获取我的订单"中看到邀请码
3. **学生B查询原订单**：通过邀请码查询订单详情
4. **学生B加入订单**：调用"加入订单"接口，is_applicant=0
5. **学生B看不到邀请码**：在自己的订单列表中，邀请码被隐藏
6. **管理员审批**：所有订单都通过审批

### 场景三：退票流程

1. **学生A创建并通过审批**：订单状态为"已通过"
2. **学生B通过邀请码加入并通过审批**：同样为"已通过"
3. **学生A申请退票**：调用"申请退票"接口
4. **级联更新验证**：学生A和学生B的订单都变为"已退票"
5. **学生B看到状态**：在订单列表中看到"✓ 已退票"标签
6. **管理员看到状态**：在管理界面看到订单状态为"已退票"

### 场景四：拒绝和撤销

1. **学生C创建订单**：状态为"审核中"
2. **管理员拒绝订单**：填写拒绝理由，订单状态变为"已拒绝"
3. **学生C看到拒绝原因**：在订单详情中看到拒绝原因

另一种情况：
1. **学生D的订单被审批通过**：状态为"已通过"
2. **管理员撤销订单**：填写撤销理由，订单状态变为"已拒绝"，车辆被释放

---

## 4. 常见错误排查

### 错误 1: 时间冲突
**现象**：审批订单时提示"车辆在该时间段有冲突"

**原因**：该车辆在指定时间段内已有其他"已通过"的订单

**解决**：选择其他车辆或查看车辆的时间冲突情况

### 错误 2: 重复车牌号
**现象**：添加车辆时提示"该车牌号已存在"

**原因**：系统中已有相同的车牌号

**解决**：修改车牌号或先删除旧车辆

### 错误 3: 权限拒绝
**现象**：调用学生接口提示"401未登录"或"403无权限"

**原因**：Token过期或权限不足

**解决**：重新登录或检查是否使用了正确的学生账号

### 错误 4: 申请人验证失败
**现象**：调用申请退票接口提示"只有原申请人可以申请退票"

**原因**：当前用户不是订单的申请人（is_applicant≠1）

**解决**：使用订单创建者的学号调用接口

---

## 5. 数据验证SQL

### 验证订单状态
```sql
SELECT order_id, student_id, status, is_applicant, invitation_code 
FROM t_order 
WHERE order_id = 1;
```

### 验证级联退票
```sql
SELECT order_id, student_id, status, is_applicant
FROM t_order 
WHERE invitation_code = 'ABC12345';
```

### 验证车辆时间冲突
```sql
SELECT COUNT(*) FROM t_order
WHERE bus_id = 1 
  AND status = '已通过'
  AND start_time <= '2023-12-25 17:00:00'
  AND end_time >= '2023-12-25 09:00:00';
```

---

## 6. 测试检查清单

- [ ] 学生能注册和登录
- [ ] 管理员能登录
- [ ] 学生能创建订单（自动生成邀请码）
- [ ] 管理员能查看和审批订单
- [ ] 管理员能添加和删除车辆
- [ ] 学生能通过邀请码加入订单
- [ ] 申请人能看到邀请码，加入者看不到
- [ ] 申请人能申请退票，加入者不能
- [ ] 申请人申请退票时，同邀请码的订单都变为"已退票"
- [ ] 车辆删除时，相关订单自动变为"已拒绝"
- [ ] 时间冲突检查正常工作

# Spring Boot 后端接口需求文档

本项目前端使用 Vue 3 + Element Plus 实现，后端需使用 Spring Boot 实现以下接口。
所有接口返回数据格式建议统一为：
```json
{
  "code": 200,
  "message": "成功",
  "data": { ... }
}
```

## 1. 认证模块 (AuthController)

### 1.1 学生注册

- **接口地址**: `/api/auth/student/register`
- **请求方式**: `POST`
- **请求参数**:
  
  ```json
  {
    "studentId": "2021001",
    "name": "张三",
    "gender": "男",
    "clazz": "计算机1班",
    "password": "123"
  }
  ```
- **功能**: 向`学生信息表`插入数据。

### 1.2 学生登录
- **接口地址**: `/api/auth/student/login`
- **请求方式**: `POST`
- **请求参数**:
  ```json
  {
    "studentId": "2021001",
    "password": "123"
  }
  ```
- **返回数据**:
  ```json
  {
    "token": "jwt-token-string",
    "userInfo": { "name": "张三", "role": "student", ... }
  }
  ```

### 1.3 管理员注册
- **接口地址**: `/api/auth/admin/register`
- **请求方式**: `POST`
- **请求参数**:
  ```json
  {
    "username": "admin",
    "name": "管理员A",
    "password": "123"
  }
  ```
- **功能**: 向`管理员信息表`插入数据。

### 1.4 管理员登录
- **接口地址**: `/api/auth/admin/login`
- **请求方式**: `POST`
- **请求参数**:
  ```json
  {
    "username": "admin",
    "password": "123"
  }
  ```
- **返回数据**:
  ```json
  {
    "token": "jwt-token-string",
    "userInfo": { "name": "管理员A", "role": "admin", ... }
  }
  ```

---

## 2. 学生功能模块 (StudentController)

### 2.1 包车（购票）
- **接口地址**: `/api/student/charter`
- **请求方式**: `POST`
- **请求头**: `Authorization: Bearer <token>`
- **请求参数**:
  ```json
  {
    "date": "2023-12-01",
    "startLocation": "学校北门",
    "endLocation": "火车站",
    "vehicleType": "客车"
  }
  ```
- **功能**:
  1. 系统随机分配一个该车型的车牌号（需确保该车在当天未被占用，或简化为随机生成/选取）。
  2. 生成6位邀请码。
  3. 在`车次表`中添加记录。
  4. 在`学生购票信息表`中添加记录（作为包车人）。
  5. 在`乘车人员信息表`中添加记录（分配座位号1）。
- **返回数据**:
  ```json
  {
    "inviteCode": "ABC123",
    "plateNumber": "京A88888"
  }
  ```

### 2.2 通过邀请码加入
- **接口地址**: `/api/student/join`
- **请求方式**: `POST`
- **请求头**: `Authorization: Bearer <token>`
- **请求参数**:
  ```json
  {
    "code": "ABC123"
  }
  ```
- **功能**:
  1. 根据邀请码查询对应的车次信息。
  2. 查询该车次已分配的座位号，计算最小未分配座位号。
  3. 在`乘车人员信息表`中添加记录。
- **返回数据**: 成功或失败信息。

### 2.3 获取我的行程
- **接口地址**: `/api/student/trips`
- **请求方式**: `GET`
- **请求头**: `Authorization: Bearer <token>`
- **功能**: 查询当前登录学生的所有乘车记录。需关联查询`车次表`、`学生购票信息表`（获取邀请码、价格等）、`司机表`（获取司机电话）。
- **返回数据**:
  ```json
  [
    {
      "plateNumber": "京A88888",
      "vehicleType": "客车",
      "date": "2023-12-01",
      "startLocation": "...",
      "endLocation": "...",
      "seatNumber": 1,
      "driverName": "李师傅",
      "driverPhone": "13800138000",
      "price": 3888.88,
      "inviteCode": "ABC123" // 如果是包车人则显示，否则可为空
    }
  ]
  ```

### 2.4 退票
- **接口地址**: `/api/student/refund/{plateNumber}`
- **请求方式**: `POST`
- **请求头**: `Authorization: Bearer <token>`
- **功能**:
  - 如果是包车人（在`学生购票信息表`中有记录且是该车次的购买者）：删除`车次表`对应记录，删除`学生购票信息表`记录，删除`乘车人员信息表`中该车次所有记录。
  - 如果是普通乘客：仅删除`乘车人员信息表`中自己的记录。

### 2.5 获取个人信息
- **接口地址**: `/api/student/profile`
- **请求方式**: `GET`
- **请求头**: `Authorization: Bearer <token>`
- **功能**: 返回当前登录学生的详细信息。

---

## 3. 管理员功能模块 (AdminController)

### 3.1 获取所有车次
- **接口地址**: `/api/admin/trips`
- **请求方式**: `GET`
- **请求头**: `Authorization: Bearer <token>`
- **功能**: 查询`车次表`所有数据，并关联查询司机姓名、当前乘车人数。
- **返回数据**:
  ```json
  [
    {
      "plateNumber": "京A88888",
      "vehicleType": "客车",
      "date": "2023-12-01",
      "startLocation": "...",
      "endLocation": "...",
      "passengerCount": 10,
      "driverName": "李师傅",
      "driverPhone": "138..."
    }
  ]
  ```

### 3.2 获取某车次乘客
- **接口地址**: `/api/admin/trips/{plateNumber}/passengers`
- **请求方式**: `GET`
- **请求头**: `Authorization: Bearer <token>`
- **功能**: 查询`乘车人员信息表`中指定车牌号的所有乘客。

### 3.3 修改车次司机
- **接口地址**: `/api/admin/trips/{plateNumber}/driver`
- **请求方式**: `PUT`
- **请求头**: `Authorization: Bearer <token>`
- **请求参数**:
  ```json
  {
    "driverPhone": "13900139000"
  }
  ```
- **功能**: 更新`学生购票信息表`（因为题目中说“修改后对应车次的所有信息（车次表、学生购票信息表）都需要修改”，虽然司机信息主要关联在购票信息表或车次表中，需根据具体表结构调整，确保数据一致性）。

### 3.4 获取所有司机
- **接口地址**: `/api/admin/drivers`
- **请求方式**: `GET`
- **请求头**: `Authorization: Bearer <token>`
- **功能**: 查询`司机表`所有数据。

### 3.5 新增司机
- **接口地址**: `/api/admin/drivers`
- **请求方式**: `POST`
- **请求头**: `Authorization: Bearer <token>`
- **请求参数**:
  ```json
  {
    "name": "王师傅",
    "phone": "13700137000"
  }
  ```
- **功能**: 向`司机表`插入数据。

# Spring Boot 后端接口说明

后端已基于 Spring Boot 3 + JPA 实现全部接口。所有接口统一返回：

```json
{
  "code": 200,
  "message": "成功",
  "data": ... // 失败时 data 为空
}
```

- 登录成功后，前端需把 `data.userInfo` 写入 `localStorage`，并在后续请求头中按角色携带 `X-Student-Id` 或 `X-Admin-Id` 以标识当前用户。
- 失败时会返回对应的 HTTP 状态码（400/401/404/500 等）以及 `{"code":400,"message":"错误信息","data":null}`。

## 1. 认证模块 `/api/auth`

| 功能 | Method + URL | 请求体 | 返回 `data` |
| --- | --- | --- | --- |
| 学生注册 | `POST /student/register` | `{ "studentId": "2021001", "name": "张三", "gender": "男", "clazz": "计科1班", "password": "123456" }` | `{ "userInfo": { "role": "student", "name": "张三", "studentId": 2021001, "clazz": "计科1班", "gender": "男" } }` |
| 学生登录 | `POST /student/login` | `{ "studentId": "2021001", "password": "123456" }` | 同上 |
| 管理员注册 | `POST /admin/register` | `{ "username": "9001", "name": "管理员A", "password": "123456" }` | `{ "userInfo": { "role": "admin", "name": "管理员A", "adminId": 9001 } }` |
| 管理员登录 | `POST /admin/login` | `{ "username": "9001", "password": "123456" }` | 同上 |

> 说明：学生/管理员账号均以阿拉伯数字存储，密码以明文写入 `student_info.manage_info` 表。

## 2. 学生模块 `/api/student`

> 所有接口都要求在请求头携带 `X-Student-Id`，后端会使用该学号读取学生信息。

### 2.1 包车 / 购票

- **接口**：`POST /charter`
- **请求体**：`{"date":"2025-01-01","startLocation":"学校北门","endLocation":"高铁站","vehicleType":"客车"}`
- **处理逻辑**：
  1. 读取登录学生信息，校验日期不能早于当天；
  2. 随机生成未占用车牌号（如 `京A12345`）和 6 位邀请码；
  3. 随机分配已有司机（如需，请先在后台新增司机）；
  4. 插入 `bus_schedules` / `student_ticket_orders` / `passenger_info`，并把座位 1 留给包车人；
  5. 价格根据 `vehicleType` 自动估算，可在 `application.properties` 用 `app.charter.price.default` 调整基准价。
- **返回数据**：

```json
{
  "inviteCode": "ABC123",
  "plateNumber": "京A56789",
  "driverName": "李师傅",
  "driverPhone": "13800138000"
}
```

### 2.2 通过邀请码加入

- **接口**：`POST /join`
- **请求体**：`{"code":"ABC123"}`
- **效果**：按最小未使用座位号自动排座，插入 `passenger_info` 并更新车次人数。重复加入会返回 400。
- **返回**：`data` 为空，`code`=200 表示成功。

### 2.3 获取我的行程

- **接口**：`GET /trips`
- **返回示例**：

```json
[
  {
    "plateNumber": "京A56789",
    "vehicleType": "客车",
    "date": "2025-01-01",
    "startLocation": "学校北门",
    "endLocation": "高铁站",
    "seatNumber": 1,
    "driverName": "李师傅",
    "driverPhone": "13800138000",
    "price": 388.88,
    "inviteCode": "ABC123" // 仅包车人可见
  }
]
```

### 2.4 退票

- **接口**：`POST /refund/{plateNumber}`
- **逻辑**：
  - 座位号 1（包车人）退票：直接删除 `bus_schedules`——数据库外键会级联删除订单与乘客；
  - 普通乘客退票：仅删除自己的 `passenger_info`，车次人数 -1。

### 2.5 查看个人资料

- **接口**：`GET /profile`
- **返回**：`{"studentId":2021001,"name":"张三","gender":"男","clazz":"计科1班"}`。

## 3. 管理员模块 `/api/admin`

> 所有接口都要求在请求头携带 `X-Admin-Id`，后端会验证该管理员是否存在。缺失或无效时会返回 400/404。

### 3.1 车次列表

- `GET /trips`
- **返回**：

```json
[
  {
    "plateNumber": "京A56789",
    "vehicleType": "客车",
    "date": "2025-01-01",
    "startLocation": "学校北门",
    "endLocation": "高铁站",
    "passengerCount": 5,
    "driverName": "李师傅",
    "driverPhone": "13800138000"
  }
]
```

### 3.2 查看单个车次乘客

- `GET /trips/{plateNumber}/passengers`
- **返回**：`[{"studentId":2021001,"name":"张三","seatNumber":1}, ...]`

### 3.3 更换司机

- `PUT /trips/{plateNumber}/driver`
- **请求体**：`{"driverPhone":"13900139000"}`
- **效果**：校验司机是否存在，并更新 `student_ticket_orders.driver`。

### 3.4 司机列表

- `GET /drivers`
- **返回**：`[{"name":"李师傅","phone":"13800138000"}]`

### 3.5 新增司机

- `POST /drivers`
- **请求体**：`{"name":"王师傅","phone":"13700137000"}`（电话唯一）
- **返回**：与列表同样结构。

## 4. 其他注意事项

1. **认证与安全**：系统使用自定义请求头 `X-Student-Id` / `X-Admin-Id` 标记身份，后端仅根据该 ID 校验数据库记录，不再返回 JWT。
2. **数据一致性**：退票、改司机等操作在事务中完成，并利用 MySQL 外键 `ON DELETE CASCADE` 同步清理关联数据。
3. **错误示例**：

   ```json
   {
     "code": 400,
     "message": "邀请码无效",
     "data": null
   }
   ```

4. **配置**：数据库信息与默认包车价已写入 `src/main/resources/application.properties`，如需修改请同步更新生产环境。

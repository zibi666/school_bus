#  后端接口需求清单（API Requirements）

本文档用于描述 **Spring Boot 后端** 需要实现的接口规范，以支撑新的前端页面功能。

------

##  一、认证模块（`AuthController`）

> 负责学生与管理员的登录、注册等基础认证功能。

| 接口路径                     | 请求方式 | 接口说明   | 请求参数示例                                               |
| ---------------------------- | -------- | ---------- | ---------------------------------------------------------- |
| `/api/auth/student/login`    | `POST`   | 学生登录   | `{ "studentId": "...", "password": "..." }`                |
| `/api/auth/student/register` | `POST`   | 学生注册   | `{ "studentId": "...", "name": "...", "password": "..." }` |
| `/api/auth/admin/login`      | `POST`   | 管理员登录 | `{ "account": "admin", "password": "..." }`                |

------

##  二、学生业务模块（`StudentController`）

> 学生端核心业务接口，包括包车申请、订单管理和个人信息查看。

| 接口路径                          | 请求方式 | 接口说明     | 请求参数示例                                                 |
| --------------------------------- | -------- | ------------ | :----------------------------------------------------------- |
| `/api/student/orders`             | `POST`   | 提交包车申请 | `{ "destination": "...", "usageTime": "...", "requestedCarType": "..." }` |
| `/api/student/orders`             | `GET`    | 获取我的订单 | 请求头携带 `Token` 或 `studentId`                            |
| `/api/student/orders/{id}/cancel` | `POST`   | 取消订单     | 无（仅“审核中”状态可调用）                                   |
| `/api/student/profile`            | `GET`    | 获取个人信息 | 无                                                           |

------

##  三、管理员业务模块（`AdminController`）

> 管理员用于订单审核、车辆管理等后台操作。

| 接口路径                         | 请求方式 | 接口说明     | 请求参数示例                                                 |
| -------------------------------- | -------- | ------------ | ------------------------------------------------------------ |
| `/api/admin/orders`              | `GET`    | 获取所有订单 | 可选参数：`?status=审核中`                                   |
| `/api/admin/orders/{id}/approve` | `POST`   | 审核通过     | `{ "busId": 1 }`                                             |
| `/api/admin/orders/{id}/reject`  | `POST`   | 审核拒绝     | `{ "rejectReason": "车辆不足" }`                             |
| `/api/admin/buses`               | `GET`    | 获取车辆列表 | 无                                                           |
| `/api/admin/buses`               | `POST`   | 新增车辆     | `{ "plateNumber": "...", "carType": "...", "driverName": "..." }` |

------

##  四、说明补充

- 所有业务接口建议统一返回格式：

```
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

- 建议：
  - 学生接口统一通过 `Token` 鉴权
  - 管理员接口单独拦截权限
  - 订单状态建议使用枚举：`审核中 / 已通过 / 已拒绝 / 已取消`

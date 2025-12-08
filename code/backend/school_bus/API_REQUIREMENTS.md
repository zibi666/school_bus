# 后端接口需求清单（API Requirements）

本文档用于描述 **Spring Boot 后端** 需要实现的接口规范，以支撑新的前端页面功能。

------

## 一、认证模块（`AuthController`）

> 负责学生与管理员的登录、注册等基础认证功能。

| 接口路径                     | 请求方式 | 接口说明   | 请求参数示例                                               |
| ---------------------------- | -------- | ---------- | ---------------------------------------------------------- |
| `/api/auth/student/login`    | `POST`   | 学生登录   | `{ "studentId": "...", "password": "..." }`                |
| `/api/auth/student/register` | `POST`   | 学生注册   | `{ "studentId": "...", "name": "...", "password": "..." }` |
| `/api/auth/admin/login`      | `POST`   | 管理员登录 | `{ "account": "admin", "password": "..." }`                |

------

## 二、学生业务模块（`StudentController`）

> 学生端核心业务接口，包括包车申请、订单管理和个人信息查看。

| 接口路径                          | 请求方式 | 接口说明     | 请求参数示例                                                 |
| --------------------------------- | -------- | ------------ | :----------------------------------------------------------- |
| `/api/student/order`              | `POST`   | 提交包车申请 | `{ "destination": "...", "usageTime": "...", "requestedCarType": "..." }` |
| `/api/student/orders/{studentId}` | `GET`    | 获取我的订单 | 无（路径参数：studentId）                                    |
| `/api/student/order/cancel/{orderId}` | `POST`   | 取消订单     | 无（仅"审核中"状态可调用）                                   |
| `/api/student/profile/{studentId}` | `GET`    | 获取个人信息 | 无（路径参数：studentId）                                    |
| `/api/student/profile/{studentId}` | `PUT`    | 修改个人信息 | `{ "name": "...", "location": "...", "password": "..." }`   |
| `/api/student/bus/{busId}`        | `GET`    | 获取车辆详情 | 无（路径参数：busId）                                        |

------

## 三、管理员业务模块（`AdminController`）

> 管理员用于订单审核、车辆管理等后台操作。

| 接口路径                         | 请求方式 | 接口说明     | 请求参数示例                                                 |
| -------------------------------- | -------- | ------------ | ------------------------------------------------------------ |
| `/api/admin/orders`              | `GET`    | 获取所有订单 | 可选参数：`?status=审核中`                                   |
| `/api/admin/order/approve`       | `POST`   | 审核通过     | `{ "orderId": 1, "busId": 1 }`                             |
| `/api/admin/order/reject`        | `POST`   | 审核拒绝     | `{ "orderId": 1, "reason": "车辆不足" }`                    |
| `/api/admin/order/revoke`        | `POST`   | 撤销订单     | `{ "orderId": 1, "reason": "用户主动撤销" }`                |
| `/api/admin/buses`               | `GET`    | 获取车辆列表 | 无                                                           |
| `/api/admin/bus`                 | `POST`   | 新增车辆     | `{ "plateNumber": "...", "carType": "...", "driverName": "..." }` |
| `/api/admin/bus/{busId}`         | `DELETE` | 删除车辆     | 无（路径参数：busId）                                        |

------

## 四、说明补充

- 所有业务接口建议统一返回格式：

```json
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
  - 撤销订单仅限"已通过"状态的订单，撤销后状态变为"已拒绝"，车辆被释放

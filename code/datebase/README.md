# 表设计

------

## 管理员信息表

**表名是 admin_info**

```sql
CREATE TABLE `admin_info` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `account` varchar(255) NOT NULL COMMENT '管理员账号',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `name` varchar(255) NOT NULL COMMENT '真实姓名',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uk_admin_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';
```

**字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| admin_id | int(11) | 管理员ID（主键，自增） |
| account | varchar(255) | 管理员账号（唯一） |
| password | varchar(255) | 登录密码 |
| name | varchar(255) | 真实姓名 |

------

## 学生信息表

**表名是 student_info**

```sql
CREATE TABLE `student_info` (
  `student_id` varchar(20) NOT NULL COMMENT '学号 (主键)',
  `name` varchar(255) NOT NULL COMMENT '学生姓名',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `location` varchar(255) NOT NULL COMMENT '所在地',
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生基本信息表';
```

**字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| student_id | varchar(20) | 学号（主键） |
| name | varchar(255) | 学生姓名 |
| password | varchar(255) | 登录密码 |
| location | varchar(255) | 所在地 |

------

## 车辆信息表

**表名是 t_bus**

```sql
CREATE TABLE `t_bus` (
  `bus_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `plate_number` varchar(255) NOT NULL COMMENT '车牌号',
  `car_type` varchar(255) NOT NULL COMMENT '车辆类型',
  `driver_name` varchar(255) NOT NULL COMMENT '司机姓名',
  `price` int(11) NOT NULL DEFAULT '500' COMMENT '单价',
  `number` varchar(255) NOT NULL COMMENT '司机号码',
  PRIMARY KEY (`bus_id`),
  UNIQUE KEY `uk_bus_plate` (`plate_number`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';
```

**字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| bus_id | int(11) | 车辆ID（主键，自增） |
| plate_number | varchar(255) | 车牌号（唯一） |
| car_type | varchar(255) | 车辆类型 |
| driver_name | varchar(255) | 司机姓名 |
| price | int(11) | 单价（默认500元） |
| number | varchar(255) | 司机号码 |

------

## 学生购票/包车订单表

**表名是 t_order**

```sql
CREATE TABLE `t_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单号',
  `student_id` varchar(20) NOT NULL COMMENT '申请人学号',
  `destination` varchar(255) NOT NULL COMMENT '目的地',
  `requested_car_type` varchar(255) NOT NULL COMMENT '需求车型',
  `bus_id` int(11) DEFAULT NULL COMMENT '分配车辆ID',
  `price` decimal(10,2) DEFAULT NULL COMMENT '订单费用',
  `status` varchar(20) DEFAULT '审核中' COMMENT '状态: 审核中/已通过/已拒绝/已退票',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝理由',
  `is_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否支付',
  `invitation_code` varchar(20) DEFAULT NULL COMMENT '邀请码',
  `start_time` datetime DEFAULT NULL COMMENT '使用开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '使用结束时间',
  `is_applicant` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否申请人(1-申请人,0-加入者)',
  PRIMARY KEY (`order_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_bus` (`bus_id`),
  KEY `idx_order_bus_time` (`bus_id`,`start_time`,`end_time`,`status`),
  KEY `idx_order_student_paid` (`student_id`,`is_paid`),
  KEY `idx_is_applicant` (`is_applicant`),
  KEY `idx_student_bus_time` (`student_id`,`bus_id`,`start_time`,`end_time`,`is_applicant`),
  KEY `idx_invitation_code_status` (`invitation_code`,`status`),
  CONSTRAINT `fk_order_bus` FOREIGN KEY (`bus_id`) REFERENCES `t_bus` (`bus_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_order_student` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='学生购票/包车订单表';
```

**字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| order_id | int(11) | 订单号（主键，自增） |
| student_id | varchar(20) | 申请人学号（外键） |
| destination | varchar(255) | 目的地 |
| requested_car_type | varchar(255) | 需求车型 |
| bus_id | int(11) | 分配车辆ID（外键） |
| price | decimal(10,2) | 订单费用 |
| status | varchar(20) | 订单状态：审核中/已通过/已拒绝/已退票 |
| reject_reason | varchar(255) | 拒绝理由 |
| is_paid | tinyint(1) | 是否支付（0-未支付，1-已支付） |
| invitation_code | varchar(20) | 邀请码（邀请码功能） |
| start_time | datetime | 使用开始时间 |
| end_time | datetime | 使用结束时间 |
| is_applicant | tinyint(4) | 是否申请人（1-申请人，0-通过邀请码加入） |

**索引说明：**
- `idx_order_bus_time`: 按车辆和时间段查询订单（检查时间冲突）
- `idx_order_student_paid`: 查询学生的支付状态订单
- `idx_is_applicant`: 快速查询申请人订单
- `idx_student_bus_time`: 学生、车辆、时间综合查询
- `idx_invitation_code_status`: 邀请码和状态查询（级联退票）

------

# 连接信息

- 数据库类型：MySQL 5.7+
- 数据库名称：**school_bus**
- 字符集：utf8mb4
- 存储引擎：InnoDB
- 支持外键约束、事务处理

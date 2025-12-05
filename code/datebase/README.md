# 表设计

------



## 学生信息表

**表名是 student_info**

```sql
CREATE TABLE `student_info` (
  `student_id` varchar(20) NOT NULL COMMENT '学号 (主键)',
  `name` varchar(50) NOT NULL COMMENT '学生姓名',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `location` varchar(100) NOT NULL COMMENT '所在地',
  PRIMARY KEY (`student_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '学生基本信息表';
```



------



## 管理员信息表

**表名是 admin_info**

```sql
CREATE TABLE `admin_info` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `account` varchar(50) NOT NULL COMMENT '管理员账号',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `name` varchar(50) NOT NULL COMMENT '真实姓名',
  PRIMARY KEY (`admin_id`),
  UNIQUE INDEX `uk_admin_account`(`account`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '管理员信息表';
```



------



## 车辆信息表

**表名是 t_bus**

```sql
CREATE TABLE `t_bus` (
  `bus_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `plate_number` varchar(20) NOT NULL COMMENT '车牌号',
  `car_type` varchar(50) NOT NULL COMMENT '车型',
  `driver_name` varchar(50) NOT NULL COMMENT '司机姓名',
  `is_active` tinyint(1) DEFAULT 1 COMMENT '车辆状态: 1-空闲, 0-使用中',
  PRIMARY KEY (`bus_id`),
  UNIQUE INDEX `uk_bus_plate`(`plate_number`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '车辆信息表';
```



------



## 学生购票 / 包车订单表

**表名是 t_order**

```sql
CREATE TABLE `t_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单号',
  `student_id` varchar(20) NOT NULL COMMENT '申请人学号',
  `destination` varchar(100) NOT NULL COMMENT '目的地',
  `usage_time` varchar(100) NOT NULL COMMENT '使用时间段',
  `requested_car_type` varchar(50) NOT NULL COMMENT '申请车型',
  `bus_id` int(11) DEFAULT NULL COMMENT '分配车辆ID',
  `price` decimal(10, 2) DEFAULT NULL COMMENT '订单费用',
  `status` varchar(20) DEFAULT '审核中' COMMENT '状态: 审核中/已通过/已拒绝',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝理由',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`order_id`),
  INDEX `idx_student`(`student_id`),
  INDEX `idx_bus`(`bus_id`),
  CONSTRAINT `fk_order_bus`
    FOREIGN KEY (`bus_id`) REFERENCES `t_bus` (`bus_id`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_order_student`
    FOREIGN KEY (`student_id`) REFERENCES `student_info` (`student_id`)
    ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '学生购票/包车订单表';
```



------



# 连接信息

- 数据库类型：腾讯云 MySQL 5.7
- 数据库名称：**school_bus**
- 字符集：utf8mb4
- 存储引擎：InnoDB
- 支持外键约束、事务处理

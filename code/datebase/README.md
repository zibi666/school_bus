# 表设计
## 学生信息表
**表名是student_info**
```sql
CREATE TABLE `student_info` (
  `stu_name` varchar(255) NOT NULL COMMENT '学生姓名',
  `stu_sex` varchar(255) NOT NULL COMMENT '学生性别',
  `stu_class` varchar(255) NOT NULL COMMENT '学生班级',
  `stu_id` int(11) NOT NULL COMMENT '学生学号',
  `stu_passwd` varchar(255) NOT NULL COMMENT '学生密码',
  PRIMARY KEY (`stu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```


## 管理员信息表

**表名是manage_info**
```sql
CREATE TABLE `manage_info` (
  `manage_name` VARCHAR ( 255 ) NOT NULL COMMENT '管理员姓名',
  `manage_id` INT ( 11 ) NOT NULL COMMENT '管理员账号',
  `manage_passwd` VARCHAR ( 255 ) NOT NULL COMMENT '管理员密码',
PRIMARY KEY ( `manage_id` ) 
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
```

## 司机信息表
**表名是driver_info**
```sql
CREATE TABLE `driver_info` (
  `driver_name` varchar(255) NOT NULL COMMENT '司机姓名',
  `driver_telephone` varchar(255) NOT NULL COMMENT '司机电话',
  PRIMARY KEY (`driver_telephone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 学生购票信息表
**表名是student_ticket_orders**
```sql
CREATE TABLE `student_ticket_orders` (
  `bus_type` varchar(255) NOT NULL COMMENT '车型',
  `bus_id` varchar(255) NOT NULL COMMENT '车牌号',
  `seat` varchar(255) NOT NULL COMMENT '座位号',
  `use_date` datetime NOT NULL COMMENT '用车日期',
  `origin` varchar(255) NOT NULL COMMENT '出发地',
  `destination` varchar(255) NOT NULL COMMENT '目的地',
  `number` int(11) NOT NULL COMMENT '人数',
  `driver` varchar(255) NOT NULL COMMENT '司机',
  `price` double NOT NULL COMMENT '价格',
  `invite_code` varchar(255) NOT NULL COMMENT '邀请码',
  PRIMARY KEY (`bus_id`),
  CONSTRAINT `student_ticket_orders_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus_schedules` (`bus_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 乘车人员信息表
**表名是passenger_info**
```sql
CREATE TABLE `passenger_info` (
  `passenger_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `bus_id` varchar(255) NOT NULL COMMENT '车牌号',
  `passenger_seat` varchar(255) NOT NULL COMMENT '乘车人员座位号',
  `passenger_number` int(11) NOT NULL COMMENT '乘车人员学号',
  `passenger_name` varchar(255) NOT NULL COMMENT '乘车人员姓名',
  PRIMARY KEY (`passenger_id`),
  KEY `passenger_number` (`passenger_number`),
  KEY `bus_id` (`bus_id`),
  CONSTRAINT `passenger_info_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `student_ticket_orders` (`bus_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `passenger_info_ibfk_2` FOREIGN KEY (`passenger_number`) REFERENCES `student_info` (`stu_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 车次表
**表名是bus_schedules**
```sql
CREATE TABLE `bus_schedules` (
  `bus_type` varchar(255) NOT NULL COMMENT '车型',
  `bus_id` varchar(255) NOT NULL COMMENT '车牌号',
  `use_date` datetime NOT NULL COMMENT '用车日期',
  `origin` varchar(255) NOT NULL COMMENT '出发地',
  `destination` varchar(255) NOT NULL COMMENT '目的地',
  `max_number` int(11) NOT NULL COMMENT '车辆座位',
  `number` int(11) NOT NULL COMMENT '还剩多少座位',
  PRIMARY KEY (`bus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```


# 连接信息
- 数据库是腾讯云Mysql5.7
- 数据库叫school_bus

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 初始化数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `school_bus`
DEFAULT CHARSET utf8mb4
COLLATE utf8mb4_general_ci;

USE `school_bus`;

-- ----------------------------
-- 2. 管理员信息表
-- ----------------------------
DROP TABLE IF EXISTS `admin_info`;
CREATE TABLE `admin_info` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `account` varchar(255) NOT NULL COMMENT '管理员账号',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `name` varchar(255) NOT NULL COMMENT '真实姓名',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uk_admin_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- ----------------------------
-- 3. 学生基本信息表
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info` (
  `student_id` varchar(20) NOT NULL COMMENT '学号 (主键)',
  `name` varchar(255) NOT NULL COMMENT '学生姓名',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `location` varchar(255) NOT NULL COMMENT '所在地',
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生基本信息表';

-- ----------------------------
-- 4. 车辆信息表
-- ----------------------------
DROP TABLE IF EXISTS `t_bus`;
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

-- ----------------------------
-- 5. 学生购票/包车订单表
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
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

-- ----------------------------
-- 6. 插入测试数据
-- ----------------------------

-- 管理员数据
INSERT INTO `admin_info` (`account`, `password`, `name`) VALUES ('admin', '123456', '系统管理员');
INSERT INTO `admin_info` (`account`, `password`, `name`) VALUES ('scheduler', '123456', '车辆调度员');

-- 学生数据
INSERT INTO `student_info` VALUES ('2023001', '张三', '123456', '南校区');
INSERT INTO `student_info` VALUES ('2023002', '李四', '123456', '北校区');
INSERT INTO `student_info` VALUES ('2023003', '王五', '123456', '东校区');

-- 车辆数据
INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `price`, `number`) 
VALUES ('浙A88888', '45座大巴', '王师傅', 1200, '13800138000');

INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `price`, `number`) 
VALUES ('浙A66666', '19座中巴', '赵师傅', 800, '13800138001');

INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `price`, `number`) 
VALUES ('浙A99999', '7座商务车', '陈师傅', 500, '13800138002');

SET FOREIGN_KEY_CHECKS = 1;
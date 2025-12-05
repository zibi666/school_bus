SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 初始化数据库（改为 school_bus）
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `school_bus`
DEFAULT CHARSET utf8mb4
COLLATE utf8mb4_general_ci;

USE `school_bus`;

-- ----------------------------
-- 2. 表结构：管理员信息表（改为 admin_info）
-- ----------------------------
DROP TABLE IF EXISTS `admin_info`;
CREATE TABLE `admin_info`  (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `account` varchar(50) NOT NULL COMMENT '管理员账号',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `name` varchar(50) NOT NULL COMMENT '真实姓名',
  PRIMARY KEY (`admin_id`),
  UNIQUE INDEX `uk_admin_account`(`account`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '管理员信息表';

-- ----------------------------
-- 3. 表结构：学生基本信息表（改为 student_info）
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info`  (
  `student_id` varchar(20) NOT NULL COMMENT '学号 (主键)',
  `name` varchar(50) NOT NULL COMMENT '学生姓名',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `location` varchar(100) NOT NULL COMMENT '所在地',
  PRIMARY KEY (`student_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '学生基本信息表';

-- ----------------------------
-- 4. 表结构：车辆信息表（保持 t_bus 不变）
-- ----------------------------
DROP TABLE IF EXISTS `t_bus`;
CREATE TABLE `t_bus`  (
  `bus_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `plate_number` varchar(20) NOT NULL COMMENT '车牌号',
  `car_type` varchar(50) NOT NULL COMMENT '车型',
  `driver_name` varchar(50) NOT NULL COMMENT '司机姓名',
  `is_active` tinyint(1) DEFAULT 1 COMMENT '车辆状态: 1-空闲, 0-使用中',
  PRIMARY KEY (`bus_id`),
  UNIQUE INDEX `uk_bus_plate`(`plate_number`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '车辆信息表';

-- ----------------------------
-- 5. 表结构：学生购票/包车订单表（保持 t_order 不变，外键已同步修改）
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
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

-- ----------------------------
-- 6. 插入测试数据
-- ----------------------------

-- A. 插入管理员数据
INSERT INTO `admin_info` (`account`, `password`, `name`)
VALUES ('admin', '123456', '系统管理员');

INSERT INTO `admin_info` (`account`, `password`, `name`)
VALUES ('scheduler', '123456', '车辆调度员李');

-- B. 插入学生数据
INSERT INTO `student_info`
VALUES ('2023001', '张三', '123456', '浙江杭州');

INSERT INTO `student_info`
VALUES ('2023002', '李四', '123456', '浙江宁波');

INSERT INTO `student_info`
VALUES ('2023003', '王五', '123456', '浙江温州');

-- C. 插入车辆数据
INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `is_active`)
VALUES ('浙A88888', '45座大巴', '王师傅', 1);

INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `is_active`)
VALUES ('浙A66666', '19座中巴', '赵师傅', 1);

INSERT INTO `t_bus` (`plate_number`, `car_type`, `driver_name`, `is_active`)
VALUES ('浙A99999', '7座商务车', '陈师傅', 0);

-- D. 插入订单数据

-- 场景1：已通过
INSERT INTO `t_order`
(`student_id`, `destination`, `usage_time`, `requested_car_type`, `bus_id`, `price`, `status`, `reject_reason`) 
VALUES
('2023001', '千岛湖风景区', '2023-12-20 全天', '45座大巴', 1, 1200.00, '已通过', NULL);

-- 场景2：审核中
INSERT INTO `t_order`
(`student_id`, `destination`, `usage_time`, `requested_car_type`, `bus_id`, `price`, `status`, `reject_reason`) 
VALUES
('2023002', '杭州萧山国际机场', '2023-12-21 08:00-12:00', '7座商务车', NULL, NULL, '审核中', NULL);

-- 场景3：已拒绝
INSERT INTO `t_order`
(`student_id`, `destination`, `usage_time`, `requested_car_type`, `bus_id`, `price`, `status`, `reject_reason`) 
VALUES
('2023003', '北京天安门', '2023-12-22 全天', '45座大巴', NULL, NULL, '已拒绝', '距离过远，超出本校包车服务范围，建议乘坐高铁。');

-- 场景4：已通过（第二次申请）
INSERT INTO `t_order`
(`student_id`, `destination`, `usage_time`, `requested_car_type`, `bus_id`, `price`, `status`, `reject_reason`) 
VALUES
('2023001', '杭州西溪湿地', '2023-12-25 下午', '19座中巴', 2, 600.00, '已通过', NULL);

SET FOREIGN_KEY_CHECKS = 1;
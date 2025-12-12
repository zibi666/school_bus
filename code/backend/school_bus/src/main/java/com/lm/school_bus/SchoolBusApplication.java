package com.lm.school_bus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校车预约系统主应用程序入口
 * 
 * 项目说明：
 * 本系统是一个校车预约与拼车平台，支持学生在线预约校车、同学之间拼车分享等功能。
 * 
 * 核心功能：
 * 1. 学生账号管理：注册/登录
 * 2. 订单管理：创建订单、支付、查看订单、退票
 * 3. 邀请码系统：生成邀请码、邀请同学加入订单、实现拼车功能
 * 4. 级联退票：申请人退票时，同邀请码下所有订单都变为"已退票"
 * 5. 管理员功能：审核订单、分配车辆、管理车辆信息、验证时间冲突
 * 
 * 技术栈：
 * - Spring Boot: Web 应用框架
 * - MyBatis: ORM 框架（XML 方式）
 * - MySQL: 数据库
 * - Lombok: Java 代码生成库
 * 
 * 项目架构：
 * - Controller: REST API 接口层
 * - Service: 业务逻辑层
 * - Mapper: 数据访问层（MyBatis）
 * - Entity: 数据模型层
 * - Config: 配置和异常处理
 * 
 * 主要数据库表：
 * - admin_info: 管理员账号表
 * - student_info: 学生账号表
 * - t_bus: 校车信息表
 * - t_order: 订单表
 * 
 * @author School Bus Team
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.lm.school_bus.mapper")
public class SchoolBusApplication {

	/**
	 * Spring Boot 应用程序启动方法
	 * 
	 * 初始化过程：
	 * 1. 扫描 @SpringBootApplication 所在包及子包中的组件
	 * 2. 自动配置 Spring Bean
	 * 3. 启动内嵌 Tomcat 服务器
	 * 4. 加载应用程序属性配置
	 * 5. 初始化所有 Bean 并注入依赖
	 * 
	 * @param args 命令行参数（可选）
	 */
	public static void main(String[] args) {
		SpringApplication.run(SchoolBusApplication.class, args);
	}

}

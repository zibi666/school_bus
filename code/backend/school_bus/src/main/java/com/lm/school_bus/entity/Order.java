package com.lm.school_bus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表 t_order
 * 核心业务逻辑：每个学生创建一个订单时，系统自动生成唯一的邀请码
 * 其他学生可以通过邀请码加入，系统会为加入者创建一条新的订单记录
 * 申请人退票时，同邀请码下的所有订单都会变为已退票
 */
@Data
public class Order {
    /** 订单 ID（主键，自增） */
    private Integer orderId;
    
    /** 学生学号（订单申请人或加入者） */
    private String studentId;
    
    /** 目的地（用车目的地址） */
    private String destination;
    
    /** 用车开始时间 */
    private LocalDateTime startTime;
    
    /** 用车结束时间 */
    private LocalDateTime endTime;
    
    /** 申请的车型（大巴、中巴、商务车） */
    private String requestedCarType;
    
    /** 分配的车辆 ID（管理员审核通过后分配） */
    private Integer busId;
    
    /** 订单价格（根据用车时长和车型自动计算） */
    private BigDecimal price;
    
    /** 订单状态
     * - "审核中"：初始状态，待管理员审核
     * - "已通过"：管理员已批准，已分配车辆
     * - "已拒绝"：管理员已拒绝
     * - "已退票"：申请人或加入者已退票
     */
    private String status = "审核中";
    
    /** 拒绝原因（仅在状态为"已拒绝"时有值） */
    private String rejectReason;
    
    /** 是否已支付（true: 已支付, false: 未支付） */
    private Boolean isPaid = false;
    
    /** 邀请码（8位随机字符串，用于其他学生加入订单）
     * 同一邀请码下可能有多条订单记录（申请人 + 加入者）
     */
    private String invitationCode;
    
    /** 是否为本人申请
     * - true: 本人申请（申请人，可以看到邀请码，可以退票）
     * - false: 通过邀请码加入（加入者，看不到邀请码，只能退出自己的订单）
     */
    private Boolean isApplicant = true;
    
    /** 关联的车辆对象（非数据库字段，用于前端显示车辆详情） */
    private Bus bus;
    
    /** 学生姓名（非数据库字段，用于前端显示乘客姓名） */
    private String studentName;
}

package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper 接口
 * 对应数据库 t_order 表的数据访问操作
 * 处理订单的增删改查、邀请码查询、时间冲突检查等复杂业务查询
 */
@Mapper
public interface OrderMapper {
    /**
     * 根据订单状态查询订单（按时间倒序）
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> selectByStatusOrderByCreateTimeDesc(String status);
    
    /**
     * 查询所有订单（按时间倒序）
     * @return 订单列表
     */
    List<Order> selectAllOrderByCreateTimeDesc();
    
    /**
     * 根据订单 ID 查询订单
     * @param orderId 订单 ID
     * @return 订单对象
     */
    Order selectById(Integer orderId);
    
    /**
     * 插入新订单
     * @param order 订单对象
     * @return 受影响的行数
     */
    int insert(Order order);
    
    /**
     * 更新订单
     * @param order 包含 orderId 和要更新的字段的订单对象
     * @return 受影响的行数
     */
    int update(Order order);
    
    /**
     * 删除订单
     * @param orderId 订单 ID
     * @return 受影响的行数
     */
    int deleteById(Integer orderId);
    
    /**
     * 查询学生的所有订单（包括自己创建的和加入的，按时间倒序）
     * @param studentId 学号
     * @return 订单列表（包含关联的车辆信息）
     */
    List<Order> selectByStudentIdOrderByCreateTimeDesc(String studentId);
    
    /**
     * 查询与指定车辆关联的所有订单
     * @param busId 车辆 ID
     * @return 订单列表
     */
    List<Order> selectByBusId(Integer busId);
    
    /**
     * 根据邀请码查询订单（返回第一条记录）
     * 用于查询原申请人的订单
     * @param invitationCode 邀请码
     * @return 订单对象（包含车辆信息），若不存在返回 null
     */
    Order selectByInvitationCode(String invitationCode);
    
    /**
     * 检查指定学生是否已经加入过某个订单
     * 用于防止重复加入同一订单
     * @param studentId 学生学号
     * @param originalOrderId 原订单 ID
     * @return 已加入的订单（如果存在则表示已加入过，返回非空；否则返回 null）
     */
    Order selectExistingJoinedOrder(@Param("studentId") String studentId, 
                                     @Param("originalOrderId") Integer originalOrderId);
    
    /**
     * 通过邀请码查询所有订单
     * 返回该邀请码下的所有订单（申请人 + 所有加入者）
     * @param invitationCode 邀请码
     * @return 邀请码对应的所有订单列表（包含车辆信息）
     */
    List<Order> selectByInvitationCodeAll(String invitationCode);
    
    /**
     * 批量更新订单状态（按邀请码）
     * 用于申请人退票时，更新同邀请码下所有订单的状态
     * @param invitationCode 邀请码
     * @param status 新状态（通常为"已退票"）
     * @return 更新的记录数
     */
    int updateStatusByInvitationCode(@Param("invitationCode") String invitationCode, 
                                      @Param("status") String status);
    
    /**
     * 检查车辆在指定时间段内是否有时间冲突
     * 用于管理员分配车辆前验证是否有已通过的订单占用该车辆
     * @param busId 车辆 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突的已通过订单数量（0 表示无冲突）
     */
    int checkTimeConflict(@Param("busId") Integer busId, 
                          @Param("startTime") LocalDateTime startTime, 
                          @Param("endTime") LocalDateTime endTime);
}

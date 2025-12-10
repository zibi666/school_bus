package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> selectByStatusOrderByCreateTimeDesc(String status);
    List<Order> selectAllOrderByCreateTimeDesc();
    Order selectById(Integer orderId);
    int insert(Order order);
    int update(Order order);
    int deleteById(Integer orderId);
    List<Order> selectByStudentIdOrderByCreateTimeDesc(String studentId);
    List<Order> selectByBusId(Integer busId);
    
    /**
     * 通过邀请码查询订单
     * @param invitationCode 邀请码
     * @return 订单对象（带车辆信息）
     */
    Order selectByInvitationCode(String invitationCode);
    
    /**
     * 检查指定学生是否已经加入过某个订单
     * @param studentId 学生学号
     * @param originalOrderId 原订单ID
     * @return 已加入的订单（如果存在）
     */
    Order selectExistingJoinedOrder(@Param("studentId") String studentId, 
                                     @Param("originalOrderId") Integer originalOrderId);
    
    /**
     * 通过邀请码查询所有订单
     * @param invitationCode 邀请码
     * @return 邀请码对应的所有订单列表
     */
    List<Order> selectByInvitationCodeAll(String invitationCode);
    
    /**
     * 批量更新订单状态
     * @param invitationCode 邀请码
     * @param status 新状态
     * @return 更新的记录数
     */
    int updateStatusByInvitationCode(@Param("invitationCode") String invitationCode, 
                                      @Param("status") String status);
    
    /**
     * 检查车辆在指定时间段内是否有时间冲突的已通过订单
     * @param busId 车辆ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突订单数量
     */
    int checkTimeConflict(@Param("busId") Integer busId, 
                          @Param("startTime") LocalDateTime startTime, 
                          @Param("endTime") LocalDateTime endTime);
}

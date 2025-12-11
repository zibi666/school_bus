package com.lm.school_bus.service;

import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.exception.BusinessException;
import com.lm.school_bus.mapper.BusMapper;
import com.lm.school_bus.mapper.OrderMapper;
import com.lm.school_bus.mapper.StudentMapper;
import com.lm.school_bus.util.PriceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BusMapper busMapper;

    public Order createOrder(Order order) {
        // 默认状态
        order.setStatus("审核中");
        
        // 生成邀请码（8位随机码）
        String invitationCode = generateInvitationCode();
        order.setInvitationCode(invitationCode);
        
        orderMapper.insert(order);
        return order;
    }
    
    /**
     * 生成邀请码：6位数字+2位字母的组合
     * 例如：ABC123DE
     */
    private String generateInvitationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    /**
     * 计算订单价格（不保存）
     */
    public BigDecimal calculateOrderPrice(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, String requestedCarType) {
        // 根据车型查询一个车辆的价格
        List<Bus> buses = busMapper.selectByCarType(requestedCarType);
        if (buses == null || buses.isEmpty()) {
            // 如果未找到对应车型，提示管理员需要添加车辆
            throw new BusinessException(400, "系统中暂无【" + requestedCarType + "】车型的车辆，请联系管理员添加");
        }
        
        Bus bus = buses.get(0);
        if (bus.getPrice() == null || bus.getPrice() <= 0) {
            throw new BusinessException(400, "车辆价格未设置，请联系管理员");
        }
        
        return PriceCalculator.calculatePrice(startTime, endTime, bus.getPrice());
    }
    
    /**
     * 支付订单
     */
    public void payOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        if (!"审核中".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，只能支付审核中的订单");
        }
        
        if (order.getIsPaid()) {
            throw new BusinessException(400, "订单已支付");
        }
        
        if (order.getPrice() == null || order.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "订单费用无效");
        }
        
        // 标记为已支付
        order.setIsPaid(true);
        orderMapper.update(order);
    }

    public List<Order> getMyOrders(String studentId) {
        return orderMapper.selectByStudentIdOrderByCreateTimeDesc(studentId);
    }

    public void cancelOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        if (!"审核中".equals(order.getStatus())) {
            throw new BusinessException(400, "只能取消审核中的订单");
        }
        // 物理删除
        orderMapper.deleteById(orderId);
    }

    public void deleteRejectedOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        if (!"已拒绝".equals(order.getStatus())) {
            throw new BusinessException(400, "只能删除已拒绝的订单");
        }
        // 物理删除已拒绝的订单
        orderMapper.deleteById(orderId);
    }

    public Student getProfile(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(400, "学生不存在");
        }
        return student;
    }

    public Bus getBus(Integer busId) {
        return busMapper.selectById(busId);
    }

    public void updateProfile(String studentId, Student student) {
        Student existing = studentMapper.selectById(studentId);
        if (existing == null) {
            throw new BusinessException(400, "学生不存在");
        }
        
        student.setStudentId(studentId);
        studentMapper.updateById(student);
    }
    
    /**
     * 通过邀请码查询订单并返回订单详情（包含车辆信息）
     * @param invitationCode 邀请码
     * @return 订单对象（包含关联的车辆信息）
     */
    public Order getOrderByInvitationCode(String invitationCode) {
        if (invitationCode == null || invitationCode.isEmpty()) {
            throw new BusinessException(400, "邀请码不能为空");
        }
        // 使用返回多行的查询，避免当同一邀请码下存在多条记录（申请人+加入者）时抛出 TooManyResults
        java.util.List<Order> list = orderMapper.selectByInvitationCodeAll(invitationCode);
        if (list == null || list.isEmpty()) {
            throw new BusinessException(404, "邀请码不存在或已过期");
        }

        // 优先选择原申请人（is_applicant = true）对应的订单
        Order order = null;
        for (Order o : list) {
            if (Boolean.TRUE.equals(o.getIsApplicant())) {
                order = o;
                break;
            }
        }
        if (order == null) {
            // 如果没有标记申请人的记录，选择第一条记录作为退路
            order = list.get(0);
        }

        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "该订单尚未被批准，无法加入");
        }

        if (order.getBusId() == null) {
            throw new BusinessException(400, "该订单还未分配车辆");
        }

        return order;
    }
    
    /**
     * 通过邀请码加入订单（创建一条新的订单记录，学号为当前用户）
     * @param invitationCode 邀请码
     * @param currentStudentId 当前学生的学号
     * @return 新创建的订单
     */
    public Order joinOrderByInvitationCode(String invitationCode, String currentStudentId) {
        if (invitationCode == null || invitationCode.isEmpty()) {
            throw new BusinessException(400, "邀请码不能为空");
        }
        
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            throw new BusinessException(400, "学号不能为空");
        }
        
        // 查询原订单
        Order originalOrder = orderMapper.selectByInvitationCode(invitationCode);
        if (originalOrder == null) {
            throw new BusinessException(404, "邀请码不存在或已过期");
        }
        
        if (!"已通过".equals(originalOrder.getStatus())) {
            throw new BusinessException(400, "该订单尚未被批准，无法加入");
        }
        
        if (originalOrder.getBusId() == null) {
            throw new BusinessException(400, "该订单还未分配车辆");
        }
        
        // 检查当前用户是否已经加入过这个订单
        Order existingOrder = orderMapper.selectExistingJoinedOrder(currentStudentId, originalOrder.getOrderId());
        if (existingOrder != null) {
            throw new BusinessException(400, "您已经加入过该订单了");
        }

        // 申请人不能加入自己的邀请码
        if (currentStudentId.equals(originalOrder.getStudentId())) {
            throw new BusinessException(400, "申请人不能加入自己的邀请码");
        }
        
        // 创建新订单（复制原订单的信息，但改变学号、isApplicant）
        Order newOrder = new Order();
        newOrder.setStudentId(currentStudentId);
        newOrder.setDestination(originalOrder.getDestination());
        newOrder.setRequestedCarType(originalOrder.getRequestedCarType());
        newOrder.setBusId(originalOrder.getBusId());
        newOrder.setPrice(originalOrder.getPrice());
        newOrder.setStatus(originalOrder.getStatus());
        newOrder.setStartTime(originalOrder.getStartTime());
        newOrder.setEndTime(originalOrder.getEndTime());
        newOrder.setIsPaid(false);  // 新加入的订单默认未支付
        newOrder.setInvitationCode(invitationCode);  // 保留原邀请码，确保邀请码加入链一致
        newOrder.setIsApplicant(false);  // 标记为非本人申请
        
        // 插入新订单
        orderMapper.insert(newOrder);
        return newOrder;
    }
    
    /**
     * 申请退票（仅允许原申请人退票）
     * @param orderId 订单ID
     * @param currentStudentId 当前学生学号
     * @return 是否退票成功
     */
    /**
     * 申请退票：退票成功后，同一邀请码的所有订单都会被标记为已退票
     * @param orderId 订单ID
     * @param currentStudentId 当前学生学号
     * @return 是否退票成功
     */
    public void refundOrder(Integer orderId, String currentStudentId) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        // 只有本人申请的订单才能退票
        if (!order.getIsApplicant()) {
            throw new BusinessException(400, "只有原申请人可以申请退票");
        }
        
        // 验证是否为申请人
        if (!currentStudentId.equals(order.getStudentId())) {
            throw new BusinessException(400, "只有申请人可以申请退票");
        }
        
        // 验证订单状态
        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已通过的订单才能退票");
        }
        
        // 验证邀请码是否存在
        if (order.getInvitationCode() == null || order.getInvitationCode().isEmpty()) {
            throw new BusinessException(400, "无效的邀请码");
        }
        
        // 批量更新该邀请码的所有订单为已退票
        int updatedCount = orderMapper.updateStatusByInvitationCode(
            order.getInvitationCode(),
            "已退票"
        );
        
        if (updatedCount == 0) {
            throw new BusinessException(400, "退票失败，请重试");
        }
    }

    /**
     * 退出通过邀请码加入的订单（仅允许加入者本人退出）
     */
    public void leaveJoinedOrder(Integer orderId, String currentStudentId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        // 只能退出自己加入的（非申请人）订单
        if (Boolean.TRUE.equals(order.getIsApplicant())) {
            throw new BusinessException(400, "只有通过邀请码加入的学生可以退出该订单");
        }
        if (!currentStudentId.equals(order.getStudentId())) {
            throw new BusinessException(400, "只能由加入该订单的学生本人执行退出操作");
        }

        // 物理删除该条记录
        orderMapper.deleteById(orderId);
    }
}

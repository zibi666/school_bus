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

/**
 * 学生服务类
 * 处理学生的订单操作、邀请码加入、退票等核心业务逻辑
 * 
 * 核心特性：
 * - 邀请码机制：每个学生创建的订单自动生成唯一邀请码
 * - 多人拼车：其他学生可通过邀请码加入，系统为加入者创建新订单记录
 * - 级联退票：申请人退票时，同邀请码下所有订单都变为已退票
 * - 权限管理：区分申请人和加入者，权限不同
 */
@Service
public class StudentService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BusMapper busMapper;

    /**
     * 创建订单
     * 系统会自动为订单生成唯一的 8 位邀请码
     * 初始状态为"审核中"
     * @param order 订单对象（需包含学号、目的地、车型、时间段等）
     * @return 创建的订单对象（包含自动生成的邀请码和 ID）
     */
    public Order createOrder(Order order) {
        // 设置默认状态
        order.setStatus("审核中");
        
        // 生成唯一邀请码
        String invitationCode = generateInvitationCode();
        order.setInvitationCode(invitationCode);
        
        // 标记为申请人
        order.setIsApplicant(true);
        
        orderMapper.insert(order);
        return order;
    }
    
    /**
     * 生成邀请码
     * 邀请码由 8 位随机大写字母和数字组成
     * 例如：ABC12345、XYZ98765 等
     * @return 8 位随机邀请码
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
     * 计算订单价格
     * 根据用车时长和车型的每小时价格自动计算总费用
     * @param startTime 用车开始时间
     * @param endTime 用车结束时间
     * @param requestedCarType 申请的车型
     * @return 订单总价格（元）
     * @throws BusinessException 如果车型不存在或价格未设置
     */
    public BigDecimal calculateOrderPrice(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, String requestedCarType) {
        // 根据车型查询可用车辆
        List<Bus> buses = busMapper.selectByCarType(requestedCarType);
        if (buses == null || buses.isEmpty()) {
            throw new BusinessException(400, "系统中暂无【" + requestedCarType + "】车型的车辆，请联系管理员添加");
        }
        
        // 取第一辆车的价格作为该车型的统一价格
        Bus bus = buses.get(0);
        if (bus.getPrice() == null || bus.getPrice() <= 0) {
            throw new BusinessException(400, "车辆价格未设置，请联系管理员");
        }
        
        // 使用工具类计算价格
        return PriceCalculator.calculatePrice(startTime, endTime, bus.getPrice());
    }
    
    /**
     * 支付订单
     * 支付后订单才能被管理员审批和分配车辆
     * @param orderId 订单 ID
     * @throws BusinessException 如果订单不存在、状态不对或已支付
     */
    public void payOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        // 只有审核中的订单才能支付
        if (!"审核中".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，只能支付审核中的订单");
        }
        
        // 不能重复支付
        if (order.getIsPaid()) {
            throw new BusinessException(400, "订单已支付");
        }
        
        // 验证价格有效性
        if (order.getPrice() == null || order.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "订单费用无效");
        }
        
        // 标记为已支付
        order.setIsPaid(true);
        orderMapper.update(order);
    }

    /**
     * 获取学生的所有订单
     * 包括学生自己创建的订单和通过邀请码加入的订单
     * @param studentId 学号
     * @return 订单列表（按时间倒序）
     */
    public List<Order> getMyOrders(String studentId) {
        return orderMapper.selectByStudentIdOrderByCreateTimeDesc(studentId);
    }

    /**
     * 取消订单
     * 仅限于"审核中"状态的订单，会物理删除订单记录
     * @param orderId 订单 ID
     * @throws BusinessException 如果订单不存在或状态不是"审核中"
     */
    public void cancelOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        // 只能取消审核中的订单
        if (!"审核中".equals(order.getStatus())) {
            throw new BusinessException(400, "只能取消审核中的订单");
        }
        
        // 物理删除
        orderMapper.deleteById(orderId);
    }

    /**
     * 删除已拒绝的订单
     * 物理删除状态为"已拒绝"的订单记录
     * @param orderId 订单 ID
     * @throws BusinessException 如果订单不存在或状态不是"已拒绝"
     */
    public void deleteRejectedOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        
        // 只能删除已拒绝的订单
        if (!"已拒绝".equals(order.getStatus())) {
            throw new BusinessException(400, "只能删除已拒绝的订单");
        }
        
        // 物理删除
        orderMapper.deleteById(orderId);
    }

    /**
     * 获取学生个人信息
     * @param studentId 学号
     * @return 学生对象
     * @throws BusinessException 如果学生不存在
     */
    public Student getProfile(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(400, "学生不存在");
        }
        return student;
    }

    /**
     * 获取车辆详情
     * @param busId 车辆 ID
     * @return 车辆对象
     */
    public Bus getBus(Integer busId) {
        return busMapper.selectById(busId);
    }

    /**
     * 更新学生个人信息
     * @param studentId 学号
     * @param student 包含要更新字段的学生对象
     * @throws BusinessException 如果学生不存在
     */
    public void updateProfile(String studentId, Student student) {
        Student existing = studentMapper.selectById(studentId);
        if (existing == null) {
            throw new BusinessException(400, "学生不存在");
        }
        
        student.setStudentId(studentId);
        studentMapper.updateById(student);
    }
    
    /**
     * 通过邀请码查询订单
     * 用于学生查看申请人的订单详情和车辆信息，决定是否加入
     * 如果同一邀请码有多条记录（申请人+加入者），优先返回申请人的订单
     * @param invitationCode 邀请码
     * @return 订单对象（包含关联的车辆信息）
     * @throws BusinessException 如果邀请码不存在、订单未通过审批或未分配车辆
     */
    public Order getOrderByInvitationCode(String invitationCode) {
        if (invitationCode == null || invitationCode.isEmpty()) {
            throw new BusinessException(400, "邀请码不能为空");
        }
        
        // 查询该邀请码下的所有订单
        java.util.List<Order> list = orderMapper.selectByInvitationCodeAll(invitationCode);
        if (list == null || list.isEmpty()) {
            throw new BusinessException(404, "邀请码不存在或已过期");
        }

        // 优先选择原申请人（is_applicant = true）的订单
        Order order = null;
        for (Order o : list) {
            if (Boolean.TRUE.equals(o.getIsApplicant())) {
                order = o;
                break;
            }
        }
        
        // 如果没有标记申请人的记录，选择第一条记录作为降级方案
        if (order == null) {
            order = list.get(0);
        }

        // 验证订单状态
        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "该订单尚未被批准，无法加入");
        }

        // 验证是否已分配车辆
        if (order.getBusId() == null) {
            throw new BusinessException(400, "该订单还未分配车辆");
        }

        return order;
    }
    
    /**
     * 通过邀请码加入订单（拼车）
     * 此操作会为加入者创建一条新的订单记录
     * 新订单使用同一个邀请码，但 studentId 和 isApplicant 不同
     * @param invitationCode 邀请码
     * @param currentStudentId 加入者的学号
     * @return 新创建的订单对象
     * @throws BusinessException 如果邀请码不存在、订单未通过、已加入过、或申请人加入自己的邀请码
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
        
        // 验证订单状态
        if (!"已通过".equals(originalOrder.getStatus())) {
            throw new BusinessException(400, "该订单尚未被批准，无法加入");
        }
        
        // 验证是否已分配车辆
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
        
        // 为加入者创建新订单记录
        Order newOrder = new Order();
        newOrder.setStudentId(currentStudentId);               // 加入者的学号
        newOrder.setDestination(originalOrder.getDestination());
        newOrder.setRequestedCarType(originalOrder.getRequestedCarType());
        newOrder.setBusId(originalOrder.getBusId());           // 使用同一车辆
        newOrder.setPrice(originalOrder.getPrice());           // 使用同一价格
        newOrder.setStatus(originalOrder.getStatus());         // 状态同步为"已通过"
        newOrder.setStartTime(originalOrder.getStartTime());
        newOrder.setEndTime(originalOrder.getEndTime());
        newOrder.setIsPaid(false);                             // 新加入者需要单独支付
        newOrder.setInvitationCode(invitationCode);            // 保留原邀请码
        newOrder.setIsApplicant(false);                        // 标记为非申请人
        
        // 插入新订单
        orderMapper.insert(newOrder);
        return newOrder;
    }
    
    /**
     * 申请退票（仅允许申请人）
     * 这是级联退票操作：申请人退票时，同邀请码下的所有订单都会被标记为"已退票"
     * @param orderId 订单 ID
     * @param currentStudentId 当前学生学号（用于权限验证）
     * @throws BusinessException 如果订单不存在、用户无权限、或订单状态不对
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
        
        // 验证权限：确保是申请人本人操作
        if (!currentStudentId.equals(order.getStudentId())) {
            throw new BusinessException(400, "只有申请人可以申请退票");
        }
        
        // 验证订单状态：只有已通过的订单才能退票
        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已通过的订单才能退票");
        }
        
        // 验证邀请码有效性
        if (order.getInvitationCode() == null || order.getInvitationCode().isEmpty()) {
            throw new BusinessException(400, "无效的邀请码");
        }
        
        // 关键操作：批量更新该邀请码下的所有订单为"已退票"
        int updatedCount = orderMapper.updateStatusByInvitationCode(
            order.getInvitationCode(),
            "已退票"
        );
        
        if (updatedCount == 0) {
            throw new BusinessException(400, "退票失败，请重试");
        }
    }

    /**
     * 退出通过邀请码加入的订单（仅限加入者）
     * 此操作只删除当前学生的订单记录，不影响其他加入者和申请人
     * @param orderId 订单 ID
     * @param currentStudentId 当前学生学号（用于权限验证）
     * @throws BusinessException 如果订单不存在或用户无权限
     */
    public void leaveJoinedOrder(Integer orderId, String currentStudentId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        // 只能退出自己加入的订单（非申请人）
        if (Boolean.TRUE.equals(order.getIsApplicant())) {
            throw new BusinessException(400, "只有通过邀请码加入的学生可以退出该订单");
        }
        
        // 验证权限：确保是该加入者本人操作
        if (!currentStudentId.equals(order.getStudentId())) {
            throw new BusinessException(400, "只能由加入该订单的学生本人执行退出操作");
        }

        // 物理删除该条订单记录
        orderMapper.deleteById(orderId);
    }
}

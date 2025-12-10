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
        
        orderMapper.insert(order);
        return order;
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
}

package com.lm.school_bus.service;

import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.entity.Student;
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
        
        // 根据车型查询价格（如果已分配车辆）
        if (order.getBusId() != null) {
            Bus bus = busMapper.selectById(order.getBusId());
            if (bus != null && bus.getPrice() != null) {
                BigDecimal price = PriceCalculator.calculatePrice(order.getUsageTime(), bus.getPrice());
                order.setPrice(price);
            }
        }
        
        orderMapper.insert(order);
        return order;
    }
    
    /**
     * 计算订单价格（不保存）
     */
    public BigDecimal calculateOrderPrice(String usageTime, String requestedCarType) {
        // 根据车型查询一个车辆的价格
        List<Bus> buses = busMapper.selectByCarType(requestedCarType);
        if (buses == null || buses.isEmpty()) {
            throw new RuntimeException("未找到对应车型");
        }
        
        Bus bus = buses.get(0);
        if (bus.getPrice() == null || bus.getPrice() <= 0) {
            throw new RuntimeException("车辆价格未设置");
        }
        
        return PriceCalculator.calculatePrice(usageTime, bus.getPrice());
    }
    
    /**
     * 支付订单
     */
    public void payOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"审核中".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 这里可以添加实际的支付逻辑
        // 暂时只是标记为已支付（可以添加一个isPaid字段）
        // 订单创建后默认就是待审核状态，支付成功后保持审核中状态
    }

    public List<Order> getMyOrders(String studentId) {
        return orderMapper.selectByStudentIdOrderByCreateTimeDesc(studentId);
    }

    public void cancelOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"审核中".equals(order.getStatus())) {
            throw new RuntimeException("只能取消审核中的订单");
        }
        // 物理删除
        orderMapper.deleteById(orderId);
    }

    public Student getProfile(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        return student;
    }

    public Bus getBus(Integer busId) {
        return busMapper.selectById(busId);
    }

    public void updateProfile(String studentId, Student student) {
        Student existing = studentMapper.selectById(studentId);
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }
        
        student.setStudentId(studentId);
        studentMapper.updateById(student);
    }
}

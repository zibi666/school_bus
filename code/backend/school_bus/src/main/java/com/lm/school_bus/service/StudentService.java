package com.lm.school_bus.service;

import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.mapper.BusMapper;
import com.lm.school_bus.mapper.OrderMapper;
import com.lm.school_bus.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

package com.lm.school_bus.service;

import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.mapper.BusMapper;
import com.lm.school_bus.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BusMapper busMapper;

    public List<Order> getAllOrders(String status) {
        if (status != null && !status.isEmpty()) {
            return orderMapper.selectByStatusOrderByCreateTimeDesc(status);
        }
        return orderMapper.selectAllOrderByCreateTimeDesc();
    }

    @Transactional
    public void approveOrder(Integer orderId, Integer busId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        Bus bus = busMapper.selectById(busId);
        if (bus == null) {
            throw new RuntimeException("车辆不存在");
        }

        if (!bus.getIsActive()) {
            throw new RuntimeException("该车辆正在使用中，无法分配");
        }

        // 更新订单
        order.setStatus("已通过");
        order.setBusId(busId);
        orderMapper.update(order);

        // 更新车辆状态
        bus.setIsActive(false);
        busMapper.update(bus);
    }

    public void rejectOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        orderMapper.update(order);
    }

    @Transactional
    public void revokeOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"已通过".equals(order.getStatus())) {
            throw new RuntimeException("只能撤销已通过的订单");
        }

        // 释放分配的车辆
        if (order.getBusId() != null) {
            Bus bus = busMapper.selectById(order.getBusId());
            if (bus != null) {
                bus.setIsActive(true);
                busMapper.update(bus);
            }
        }

        // 将订单状态改为已拒绝
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        order.setBusId(null);
        orderMapper.update(order);
    }

    public List<Bus> getAllBuses() {
        return busMapper.selectAll();
    }

    public Bus addBus(Bus bus) {
        busMapper.insert(bus);
        return bus;
    }

    public void deleteBus(Integer busId) {
        busMapper.deleteById(busId);
    }
}

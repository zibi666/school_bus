package com.lm.school_bus.service;

import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.exception.BusinessException;
import com.lm.school_bus.mapper.BusMapper;
import com.lm.school_bus.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BusMapper busMapper;

    public List<Order> getAllOrders(String status) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderMapper.selectByStatusOrderByCreateTimeDesc(status);
        } else {
            orders = orderMapper.selectAllOrderByCreateTimeDesc();
        }
        // 过滤掉未支付的订单，管理员只能看到已支付的订单
        return orders.stream()
                .filter(order -> order.getIsPaid() != null && order.getIsPaid())
                .toList();
    }

    @Transactional
    public void approveOrder(Integer orderId, Integer busId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        Bus bus = busMapper.selectById(busId);
        if (bus == null) {
            throw new BusinessException(404, "车辆不存在");
        }

        // 检查车辆在该时间段内是否有时间冲突
        if (order.getStartTime() != null && order.getEndTime() != null) {
            int conflictCount = orderMapper.checkTimeConflict(busId, order.getStartTime(), order.getEndTime());
            if (conflictCount > 0) {
                throw new BusinessException(400, "该车辆在该时间段内已被占用，请选择其他车辆或时间");
            }
        }

        // 更新订单
        order.setStatus("已通过");
        order.setBusId(busId);
        orderMapper.update(order);
    }

    public void rejectOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        orderMapper.update(order);
    }

    @Transactional
    public void revokeOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "只能撤销已通过的订单");
        }

        // 将订单状态改为已拒绝（不再需要释放车辆状态，因为使用时间冲突检查）
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        order.setBusId(null);
        orderMapper.update(order);
    }

    public List<Bus> getAllBuses() {
        return busMapper.selectAll();
    }

    public Bus addBus(Bus bus) {
        // 检查车牌号是否重复
        Bus existingBus = busMapper.selectByPlateNumber(bus.getPlateNumber());
        if (existingBus != null) {
            throw new BusinessException(400, "该车牌号已存在，请勿添加重复车牌号");
        }
        
        // 根据车型自动设置价格
        Integer autoPrice = getAutoPriceByCarType(bus.getCarType());
        if (autoPrice == null) {
            throw new BusinessException(400, "无效的车型");
        }
        bus.setPrice(autoPrice);
        
        busMapper.insert(bus);
        return bus;
    }
    
    /**
     * 根据车型获取自动价格
     */
    private Integer getAutoPriceByCarType(String carType) {
        if (carType == null) return null;
        return switch (carType) {
            case "大巴" -> 800;
            case "中巴" -> 600;
            case "商务车" -> 1000;
            default -> null;
        };
    }

    @Transactional
    public void deleteBus(Integer busId) {
        // 查询所有与该车辆关联的订单
        List<Order> orders = orderMapper.selectByBusId(busId);
        
        // 将所有关联的订单更新为已拒绝状态
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                order.setStatus("已拒绝");
                order.setRejectReason("车辆已删除");
                orderMapper.update(order);
            }
        }
        
        // 删除车辆
        busMapper.deleteById(busId);
    }

    /**
     * 检查车辆在指定时间段是否可用
     * @param busId 车辆ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true表示可用，false表示有冲突
     */
    public boolean checkBusAvailability(Integer busId, LocalDateTime startTime, LocalDateTime endTime) {
        Bus bus = busMapper.selectById(busId);
        if (bus == null) {
            throw new BusinessException(404, "车辆不存在");
        }
        
        // 检查是否有时间冲突的已通过订单
        int conflictCount = orderMapper.checkTimeConflict(busId, startTime, endTime);
        return conflictCount == 0;
    }
}

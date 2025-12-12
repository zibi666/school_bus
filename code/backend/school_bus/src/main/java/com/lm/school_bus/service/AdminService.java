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

/**
 * 管理员服务类
 * 处理管理员的订单审批、车辆管理等业务逻辑
 * 
 * 核心业务：
 * - 订单审批：管理员审批通过订单并分配车辆
 * - 订单拒绝：拒绝未通过的订单
 * - 订单撤销：撤销已通过的订单
 * - 车辆管理：增删改查车辆
 * - 时间冲突检查：确保同一车辆不会在同一时间被分配给多个订单
 */
@Service
public class AdminService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BusMapper busMapper;

    /**
     * 获取所有订单（可按状态过滤）
     * 管理员只能看到已支付的订单
     * @param status 订单状态（可为 null 表示不过滤）
     * @return 订单列表
     */
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

    /**
     * 审批订单并分配车辆
     * 此操作为事务操作，确保订单和车辆状态一致
     * @param orderId 订单 ID
     * @param busId 要分配的车辆 ID
     * @throws BusinessException 如果订单或车辆不存在，或时间冲突
     */
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

        // 更新订单状态为"已通过"并关联车辆
        order.setStatus("已通过");
        order.setBusId(busId);
        orderMapper.update(order);
    }

    /**
     * 拒绝订单
     * @param orderId 订单 ID
     * @param reason 拒绝原因
     * @throws BusinessException 如果订单不存在
     */
    public void rejectOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        orderMapper.update(order);
    }

    /**
     * 撤销已批准的订单
     * 此操作为事务操作，确保不会遗留孤立的订单状态
     * @param orderId 订单 ID
     * @param reason 撤销原因
     * @throws BusinessException 如果订单不存在或状态不是"已通过"
     */
    @Transactional
    public void revokeOrder(Integer orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"已通过".equals(order.getStatus())) {
            throw new BusinessException(400, "只能撤销已通过的订单");
        }

        // 将订单状态改为已拒绝，释放车辆
        order.setStatus("已拒绝");
        order.setRejectReason(reason);
        order.setBusId(null);
        orderMapper.update(order);
    }

    /**
     * 获取所有车辆
     * @return 车辆列表
     */
    public List<Bus> getAllBuses() {
        return busMapper.selectAll();
    }

    /**
     * 添加新车辆
     * 价格会根据车型自动计算
     * @param bus 车辆对象（需包含车牌号、车型、司机姓名、司机电话）
     * @return 新建的车辆对象（包含自动生成的 ID 和价格）
     * @throws BusinessException 如果车牌号已存在或车型无效
     */
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
     * - 大巴: 800 元/小时
     * - 中巴: 600 元/小时
     * - 商务车: 1000 元/小时
     * @param carType 车型
     * @return 价格（元/小时），如果车型无效返回 null
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

    /**
     * 删除车辆
     * 删除前会检查是否有关联的订单，有则将这些订单状态改为"已拒绝"
     * @param busId 车辆 ID
     * @throws BusinessException 如果车辆不存在
     */
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
     * 检查车辆在指定时间段内是否可用
     * @param busId 车辆 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true 表示可用（没有冲突），false 表示不可用（有时间冲突）
     * @throws BusinessException 如果车辆不存在
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

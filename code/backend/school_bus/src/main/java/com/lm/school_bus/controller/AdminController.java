package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员的订单管理、车辆管理等接口
 * 
 * 核心功能：
 * - 查看所有订单
 * - 审批订单（分配车辆）
 * - 拒绝订单
 * - 撤销已批准的订单
 * - 车辆增删改查
 * - 检查车辆可用性
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有订单（分页展示，按时间倒序）
     * @return 订单列表
     */
    @GetMapping("/orders")
    public ApiResponse<List<Order>> getAllOrders() {
        List<Order> orders = adminService.getAllOrders(null);
        return ApiResponse.success("获取成功", orders);
    }

    /**
     * 审批通过订单并分配车辆
     * 此操作会将订单状态改为"已通过"，并关联指定的车辆
     * @param params 包含 orderId（订单 ID）和 busId（车辆 ID）
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/approve")
    public ApiResponse<Void> approveOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        Integer busId = (Integer) params.get("busId");
        
        adminService.approveOrder(orderId, busId);
        return ApiResponse.success("审批通过", null);
    }

    /**
     * 拒绝订单
     * 此操作会将订单状态改为"已拒绝"，并记录拒绝原因
     * @param params 包含 orderId（订单 ID）和 reason（拒绝原因）
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/reject")
    public ApiResponse<Void> rejectOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        String reason = (String) params.get("reason");
        
        adminService.rejectOrder(orderId, reason);
        return ApiResponse.success("已拒绝", null);
    }

    /**
     * 撤销已批准的订单
     * 此操作将已通过的订单改为"已拒绝"状态，释放已分配的车辆
     * @param params 包含 orderId（订单 ID）和 reason（撤销原因）
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/revoke")
    public ApiResponse<Void> revokeOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        String reason = (String) params.get("reason");
        
        adminService.revokeOrder(orderId, reason);
        return ApiResponse.success("订单已撤销", null);
    }

    /**
     * 获取所有车辆
     * @return 车辆列表
     */
    @GetMapping("/buses")
    public ApiResponse<List<Bus>> getAllBuses() {
        List<Bus> buses = adminService.getAllBuses();
        return ApiResponse.success("获取成功", buses);
    }

    /**
     * 添加新车辆
     * 价格会根据车型自动设置（大巴: 800元/小时，中巴: 600元/小时，商务车: 1000元/小时）
     * @param bus 包含车牌号、车型、司机姓名、司机电话的车辆对象
     * @return 新建的车辆对象（包含自动生成的 ID）
     */
    @PostMapping("/bus")
    public ApiResponse<Bus> addBus(@RequestBody Bus bus) {
        Bus newBus = adminService.addBus(bus);
        return ApiResponse.success("添加成功", newBus);
    }

    /**
     * 删除车辆
     * 删除车辆时，会自动将该车辆关联的所有订单改为"已拒绝"状态
     * @param busId 车辆 ID
     * @return 成功则返回成功信息
     */
    @DeleteMapping("/bus/{busId}")
    public ApiResponse<Void> deleteBus(@PathVariable Integer busId) {
        adminService.deleteBus(busId);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 检查车辆在指定时间段内是否可用
     * 用于管理员分配车辆前验证时间冲突
     * @param params 包含 busId（车辆 ID）、startTime（开始时间）、endTime（结束时间）
     * @return 返回可用性结果和时间段信息
     */
    @PostMapping("/bus/availability")
    public ApiResponse<Map<String, Object>> checkBusAvailability(@RequestBody Map<String, String> params) {
        Integer busId = Integer.parseInt(params.get("busId"));
        String startTimeStr = params.get("startTime");
        String endTimeStr = params.get("endTime");
        
        java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startTimeStr);
        java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endTimeStr);
        
        boolean isAvailable = adminService.checkBusAvailability(busId, startTime, endTime);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("busId", busId);
        result.put("isAvailable", isAvailable);
        result.put("startTime", startTimeStr);
        result.put("endTime", endTimeStr);
        
        return ApiResponse.success("查询成功", result);
    }
}

package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/orders")
    public ApiResponse<List<Order>> getAllOrders() {
        List<Order> orders = adminService.getAllOrders(null);
        return ApiResponse.success("获取成功", orders);
    }

    @PostMapping("/order/approve")
    public ApiResponse<Void> approveOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        Integer busId = (Integer) params.get("busId");
        
        adminService.approveOrder(orderId, busId);
        return ApiResponse.success("审批通过", null);
    }

    @PostMapping("/order/reject")
    public ApiResponse<Void> rejectOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        String reason = (String) params.get("reason");
        
        adminService.rejectOrder(orderId, reason);
        return ApiResponse.success("已拒绝", null);
    }

    @PostMapping("/order/revoke")
    public ApiResponse<Void> revokeOrder(@RequestBody Map<String, Object> params) {
        Integer orderId = (Integer) params.get("orderId");
        String reason = (String) params.get("reason");
        
        adminService.revokeOrder(orderId, reason);
        return ApiResponse.success("订单已撤销", null);
    }

    @GetMapping("/buses")
    public ApiResponse<List<Bus>> getAllBuses() {
        List<Bus> buses = adminService.getAllBuses();
        return ApiResponse.success("获取成功", buses);
    }

    @PostMapping("/bus")
    public ApiResponse<Bus> addBus(@RequestBody Bus bus) {
        Bus newBus = adminService.addBus(bus);
        return ApiResponse.success("添加成功", newBus);
    }

    @DeleteMapping("/bus/{busId}")
    public ApiResponse<Void> deleteBus(@PathVariable Integer busId) {
        adminService.deleteBus(busId);
        return ApiResponse.success("删除成功", null);
    }

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

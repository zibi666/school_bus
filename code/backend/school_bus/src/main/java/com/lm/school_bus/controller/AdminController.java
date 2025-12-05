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
}

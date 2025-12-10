package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/profile/{studentId}")
    public ApiResponse<Student> getProfile(@PathVariable String studentId) {
        Student student = studentService.getProfile(studentId);
        return ApiResponse.success("获取成功", student);
    }

    @PostMapping("/order")
    public ApiResponse<Order> createOrder(@RequestBody Map<String, String> params) {
        Order order = new Order();
        order.setStudentId(params.get("studentId"));
        order.setDestination(params.get("destination"));
        order.setRequestedCarType(params.get("requestedCarType"));
        
        // 接收前端计算好的价格
        String priceStr = params.get("price");
        if (priceStr != null) {
            try {
                order.setPrice(new java.math.BigDecimal(priceStr));
            } catch (Exception e) {
                return ApiResponse.error(400, "价格格式错误");
            }
        }
        
        // 解析前端传递的开始和结束时间（用于时间冲突检查）
        String startTimeStr = params.get("startTime");
        String endTimeStr = params.get("endTime");
        if (startTimeStr != null && endTimeStr != null) {
            order.setStartTime(java.time.LocalDateTime.parse(startTimeStr));
            order.setEndTime(java.time.LocalDateTime.parse(endTimeStr));
        }
        
        Order createdOrder = studentService.createOrder(order);
        return ApiResponse.success("预约成功", createdOrder);
    }

    @GetMapping("/orders/{studentId}")
    public ApiResponse<List<Order>> getMyOrders(@PathVariable String studentId) {
        List<Order> orders = studentService.getMyOrders(studentId);
        return ApiResponse.success("获取成功", orders);
    }

    @PostMapping("/order/cancel/{orderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable Integer orderId) {
        studentService.cancelOrder(orderId);
        return ApiResponse.success("取消成功", null);
    }

    @DeleteMapping("/order/{orderId}")
    public ApiResponse<Void> deleteRejectedOrder(@PathVariable Integer orderId) {
        studentService.deleteRejectedOrder(orderId);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/bus/{busId}")
    public ApiResponse<Bus> getBus(@PathVariable Integer busId) {
        Bus bus = studentService.getBus(busId);
        return ApiResponse.success("获取成功", bus);
    }

    @PutMapping("/profile/{studentId}")
    public ApiResponse<Void> updateProfile(@PathVariable String studentId, @RequestBody Student student) {
        studentService.updateProfile(studentId, student);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 计算订单价格
     */
    @PostMapping("/order/calculate-price")
    public ApiResponse<Map<String, Object>> calculatePrice(@RequestBody Map<String, String> params) {
        String startTimeStr = params.get("startTime");
        String endTimeStr = params.get("endTime");
        String requestedCarType = params.get("requestedCarType");
        
        if (startTimeStr == null || endTimeStr == null) {
            return ApiResponse.error(400, "开始和结束时间不能为空");
        }
        
        java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startTimeStr);
        java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endTimeStr);
        
        java.math.BigDecimal price = studentService.calculateOrderPrice(startTime, endTime, requestedCarType);
        double hours = com.lm.school_bus.util.PriceCalculator.calculateHours(startTime, endTime);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("price", price);
        result.put("hours", hours);
        result.put("formattedHours", com.lm.school_bus.util.PriceCalculator.formatHours(hours));
        result.put("timeRange", com.lm.school_bus.util.PriceCalculator.formatTimeRange(startTime, endTime));
        
        return ApiResponse.success("计算成功", result);
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/order/pay/{orderId}")
    public ApiResponse<Void> payOrder(@PathVariable Integer orderId) {
        studentService.payOrder(orderId);
        return ApiResponse.success("支付成功", null);
    }
    
    /**
     * 通过邀请码查询订单并获取车辆信息
     */
    @GetMapping("/order/by-invitation-code/{invitationCode}")
    public ApiResponse<Order> getOrderByInvitationCode(@PathVariable String invitationCode) {
        Order order = studentService.getOrderByInvitationCode(invitationCode);
        return ApiResponse.success("获取成功", order);
    }
    
    /**
     * 通过邀请码加入订单
     */
    @PostMapping("/order/join-by-invitation-code/{invitationCode}")
    public ApiResponse<Order> joinOrderByInvitationCode(@PathVariable String invitationCode, @RequestParam String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return ApiResponse.error(401, "请先登录");
        }
        Order newOrder = studentService.joinOrderByInvitationCode(invitationCode, studentId);
        return ApiResponse.success("加入成功", newOrder);
    }
    
    /**
     * 申请退票（仅允许原申请人退票，同时该邀请码下的所有订单都标记为已退票）
     */
    @PostMapping("/order/refund/{orderId}")
    public ApiResponse<Void> refundOrder(@PathVariable Integer orderId, @RequestParam String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return ApiResponse.error(401, "请先登录");
        }
        studentService.refundOrder(orderId, studentId);
        return ApiResponse.success("退票成功", null);
    }
}

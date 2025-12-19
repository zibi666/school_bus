package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Bus;
import com.lm.school_bus.entity.Order;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.service.StudentService;
import com.lm.school_bus.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生控制器
 * 处理学生的订单操作、邀请码加入等核心功能
 * 
 * 核心功能：
 * - 订单创建和管理：创建订单、查看订单、取消订单
 * - 邀请码加入：通过邀请码加入他人订单
 * - 退票管理：申请人退票（级联退票）、加入者退出
 * - 支付功能：支付订单
 * - 学生资料管理：查看和更新个人信息
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 获取学生个人信息
     * @param studentId 学号
     * @return 学生对象
     */
    @GetMapping("/profile/{studentId}")
    public ApiResponse<Student> getProfile(@PathVariable String studentId) {
        Student student = studentService.getProfile(studentId);
        return ApiResponse.success("获取成功", student);
    }

    /**
     * 创建订单（预约用车）
     * 系统会自动生成唯一的邀请码
     * @param params 包含 studentId、destination、requestedCarType、startTime、endTime、price
     * @return 创建的订单对象（包含邀请码）
     */
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

    /**
     * 获取学生的所有订单
     * @param studentId 学号
     * @return 订单列表（包含关联的车辆信息）
     */
    @GetMapping("/orders/{studentId}")
    public ApiResponse<List<Order>> getMyOrders(@PathVariable String studentId) {
        List<Order> orders = studentService.getMyOrders(studentId);
        return ApiResponse.success("获取成功", orders);
    }

    /**
     * 取消订单（仅限于"审核中"状态的订单）
     * 此操作会物理删除订单记录
     * @param orderId 订单 ID
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/cancel/{orderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable Integer orderId) {
        studentService.cancelOrder(orderId);
        return ApiResponse.success("取消成功", null);
    }

    /**
     * 删除已拒绝的订单
     * 此操作会物理删除状态为"已拒绝"的订单记录
     * @param orderId 订单 ID
     * @return 成功则返回成功信息
     */
    @DeleteMapping("/order/{orderId}")
    public ApiResponse<Void> deleteRejectedOrder(@PathVariable Integer orderId) {
        studentService.deleteRejectedOrder(orderId);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 获取车辆详情
     * @param busId 车辆 ID
     * @return 车辆对象
     */
    @GetMapping("/bus/{busId}")
    public ApiResponse<Bus> getBus(@PathVariable Integer busId) {
        Bus bus = studentService.getBus(busId);
        return ApiResponse.success("获取成功", bus);
    }

    /**
     * 更新学生个人信息
     * @param studentId 学号
     * @param student 包含要更新的字段的学生对象
     * @return 成功则返回成功信息
     */
    @PutMapping("/profile/{studentId}")
    public ApiResponse<Void> updateProfile(@PathVariable String studentId, @RequestBody Student student) {
        studentService.updateProfile(studentId, student);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 计算订单价格
     * 根据用车时长和车型自动计算费用
     * @param params 包含 startTime、endTime、requestedCarType
     * @return 包含 price、hours、formattedHours、timeRange 的结果
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
     * 支付后订单才能被管理员审批
     * @param orderId 订单 ID
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/pay/{orderId}")
    public ApiResponse<Void> payOrder(@PathVariable Integer orderId) {
        studentService.payOrder(orderId);
        return ApiResponse.success("支付成功", null);
    }
    
    /**
     * 获取同一邀请码下的所有乘客
     * @param invitationCode 邀请码
     * @return 订单列表（代表所有乘客）
     */
    @GetMapping("/order/passengers/{invitationCode}")
    public ApiResponse<List<Order>> getPassengersByInvitationCode(@PathVariable String invitationCode) {
        List<Order> passengers = studentService.getPassengersByInvitationCode(invitationCode);
        return ApiResponse.success("获取成功", passengers);
    }

    /**
     * 通过邀请码查询订单
     * 用于学生通过邀请码加入订单前，查看订单详情和车辆信息
     * @param invitationCode 邀请码
     * @return 订单对象（包含车辆详情）
     */
    @GetMapping("/order/by-invitation-code/{invitationCode}")
    public ApiResponse<Order> getOrderByInvitationCode(@PathVariable String invitationCode) {
        Order order = studentService.getOrderByInvitationCode(invitationCode);
        return ApiResponse.success("获取成功", order);
    }
    
    /**
     * 通过邀请码加入订单（拼车）
     * 系统会为加入者创建一条新的订单记录，使用同一个邀请码
     * @param invitationCode 邀请码
     * @param studentId 加入者的学号
     * @return 新创建的订单对象
     */
    @PostMapping("/order/join-by-invitation-code/{invitationCode}")
    public ApiResponse<Order> joinOrderByInvitationCode(@PathVariable String invitationCode, @RequestParam String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return ApiResponse.error(401, "请先登录");
        }
        // 额外检查：申请人不能加入自己的邀请码，提前返回友好错误信息
        try {
            Order existing = studentService.getOrderByInvitationCode(invitationCode);
            if (existing != null && studentId.equals(existing.getStudentId())) {
                return ApiResponse.error(400, "申请人不能加入自己的车辆");
            }
        } catch (BusinessException e) {
            // 如果邀请码不存在或未通过等，直接把业务错误返回给前端
            return ApiResponse.error(e.getCode(), e.getMessage());
        }

        Order newOrder = studentService.joinOrderByInvitationCode(invitationCode, studentId);
        return ApiResponse.success("加入成功", newOrder);
    }
    
    /**
     * 申请退票（仅允许申请人）
     * 申请人退票时，同邀请码下的所有订单都会被标记为"已退票"
     * @param orderId 订单 ID
     * @param studentId 当前学生学号（用于权限验证）
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/refund/{orderId}")
    public ApiResponse<Void> refundOrder(@PathVariable Integer orderId, @RequestParam String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return ApiResponse.error(401, "请先登录");
        }
        studentService.refundOrder(orderId, studentId);
        return ApiResponse.success("退票成功", null);
    }

    /**
     * 退出通过邀请码加入的订单（仅限加入者）
     * 此操作只影响加入者自己的订单记录，不影响其他人
     * @param orderId 订单 ID
     * @param studentId 当前学生学号（用于权限验证）
     * @return 成功则返回成功信息
     */
    @PostMapping("/order/leave/{orderId}")
    public ApiResponse<Void> leaveOrder(@PathVariable Integer orderId, @RequestParam String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return ApiResponse.error(401, "请先登录");
        }
        studentService.leaveJoinedOrder(orderId, studentId);
        return ApiResponse.success("已退出包车", null);
    }
}

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
        order.setUsageTime(params.get("usageTime"));
        order.setRequestedCarType(params.get("requestedCarType"));
        
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

    @GetMapping("/bus/{busId}")
    public ApiResponse<Bus> getBus(@PathVariable Integer busId) {
        Bus bus = studentService.getBus(busId);
        return ApiResponse.success("获取成功", bus);
    }
}

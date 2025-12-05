package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Admin;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/student/login")
    public ApiResponse<Student> studentLogin(@RequestBody Map<String, String> params) {
        Student student = authService.studentLogin(params.get("studentId"), params.get("password"));
        return ApiResponse.success("登录成功", student);
    }

    @PostMapping("/student/register")
    public ApiResponse<Student> studentRegister(@RequestBody Student student) {
        Student newStudent = authService.studentRegister(student);
        return ApiResponse.success("注册成功", newStudent);
    }

    @PostMapping("/admin/login")
    public ApiResponse<Admin> adminLogin(@RequestBody Map<String, String> params) {
        Admin admin = authService.adminLogin(params.get("account"), params.get("password"));
        return ApiResponse.success("登录成功", admin);
    }
}

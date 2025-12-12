package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.entity.Admin;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理学生和管理员的登录、注册等认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 学生登录
     * @param params 包含 studentId 和 password 的 Map
     * @return 登录成功返回学生信息，失败抛异常
     */
    @PostMapping("/student/login")
    public ApiResponse<Student> studentLogin(@RequestBody Map<String, String> params) {
        Student student = authService.studentLogin(params.get("studentId"), params.get("password"));
        return ApiResponse.success("登录成功", student);
    }

    /**
     * 学生注册
     * @param student 包含 studentId、name、password、location 的学生对象
     * @return 注册成功返回新建的学生对象
     */
    @PostMapping("/student/register")
    public ApiResponse<Student> studentRegister(@RequestBody Student student) {
        Student newStudent = authService.studentRegister(student);
        return ApiResponse.success("注册成功", newStudent);
    }

    /**
     * 管理员登录
     * @param params 包含 account 和 password 的 Map
     * @return 登录成功返回管理员信息，失败抛异常
     */
    @PostMapping("/admin/login")
    public ApiResponse<Admin> adminLogin(@RequestBody Map<String, String> params) {
        Admin admin = authService.adminLogin(params.get("account"), params.get("password"));
        return ApiResponse.success("登录成功", admin);
    }
}

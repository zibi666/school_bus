package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.dto.auth.*;
import com.lm.school_bus.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/student/register")
    public ApiResponse<AuthResponse> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return ApiResponse.success(authService.registerStudent(request));
    }

    @PostMapping("/student/login")
    public ApiResponse<AuthResponse> loginStudent(@Valid @RequestBody StudentLoginRequest request) {
        return ApiResponse.success(authService.loginStudent(request));
    }

    @PostMapping("/admin/register")
    public ApiResponse<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ApiResponse.success(authService.registerAdmin(request));
    }

    @PostMapping("/admin/login")
    public ApiResponse<AuthResponse> loginAdmin(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(authService.loginAdmin(request));
    }
}

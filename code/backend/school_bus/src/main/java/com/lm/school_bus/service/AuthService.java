package com.lm.school_bus.service;

import com.lm.school_bus.dto.auth.*;
import com.lm.school_bus.entity.Admin;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.exception.BadRequestException;
import com.lm.school_bus.exception.ResourceNotFoundException;
import com.lm.school_bus.repository.AdminRepository;
import com.lm.school_bus.repository.StudentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(StudentRepository studentRepository,
                       AdminRepository adminRepository) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public AuthResponse registerStudent(StudentRegisterRequest request) {
        Long studentId = Long.parseLong(request.getStudentId());
        if (studentRepository.existsByStudentId(studentId)) {
            throw new BadRequestException("学号已存在");
        }
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setClazz(request.getClazz());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        studentRepository.save(student);
        return buildStudentAuthResponse(student);
    }

    public AuthResponse loginStudent(StudentLoginRequest request) {
        Long studentId = Long.parseLong(request.getStudentId());
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在"));
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        return buildStudentAuthResponse(student);
    }

    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        Integer adminId = Integer.parseInt(request.getUsername());
        if (adminRepository.existsByAdminId(adminId)) {
            throw new BadRequestException("管理员账号已存在");
        }
        Admin admin = new Admin();
        admin.setAdminId(adminId);
        admin.setName(request.getName());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminRepository.save(admin);
        return buildAdminAuthResponse(admin);
    }

    public AuthResponse loginAdmin(AdminLoginRequest request) {
        Integer adminId = Integer.parseInt(request.getUsername());
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("管理员不存在"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        return buildAdminAuthResponse(admin);
    }

    private AuthResponse buildStudentAuthResponse(Student student) {
        UserInfo userInfo = UserInfo.builder()
                .role("student")
                .name(student.getName())
                .studentId(student.getStudentId())
                .clazz(student.getClazz())
                .gender(student.getGender())
                .build();
        return new AuthResponse(userInfo);
    }

    private AuthResponse buildAdminAuthResponse(Admin admin) {
        UserInfo userInfo = UserInfo.builder()
                .role("admin")
                .name(admin.getName())
                .adminId(admin.getAdminId())
                .build();
        return new AuthResponse(userInfo);
    }
}

package com.lm.school_bus.service;

import com.lm.school_bus.entity.Admin;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.exception.BusinessException;
import com.lm.school_bus.mapper.AdminMapper;
import com.lm.school_bus.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AdminMapper adminMapper;

    public Student studentLogin(String studentId, String password) {
        Student student = studentMapper.selectById(studentId);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        throw new BusinessException(401, "学号或密码错误");
    }

    public Student studentRegister(Student student) {
        if (studentMapper.countById(student.getStudentId()) > 0) {
            throw new BusinessException(400, "该学号已注册");
        }
        studentMapper.insert(student);
        return student;
    }

    public Admin adminLogin(String account, String password) {
        Admin admin = adminMapper.selectByAccount(account);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        throw new BusinessException(401, "账号或密码错误");
    }
}

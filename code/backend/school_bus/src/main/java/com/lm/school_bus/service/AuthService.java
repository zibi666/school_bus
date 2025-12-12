package com.lm.school_bus.service;

import com.lm.school_bus.entity.Admin;
import com.lm.school_bus.entity.Student;
import com.lm.school_bus.exception.BusinessException;
import com.lm.school_bus.mapper.AdminMapper;
import com.lm.school_bus.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 * 处理学生和管理员的登录、注册业务逻辑
 * 
 * 注意：当前实现使用明文密码存储，实际生产环境应使用加密存储
 */
@Service
public class AuthService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 学生登录
     * @param studentId 学号
     * @param password 密码
     * @return 登录成功的学生对象
     * @throws BusinessException 学号不存在或密码错误时抛出 401 异常
     */
    public Student studentLogin(String studentId, String password) {
        Student student = studentMapper.selectById(studentId);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        throw new BusinessException(401, "学号或密码错误");
    }

    /**
     * 学生注册
     * @param student 包含学号、姓名、密码、位置的新学生对象
     * @return 注册成功的学生对象
     * @throws BusinessException 学号已存在时抛出 400 异常
     */
    public Student studentRegister(Student student) {
        if (studentMapper.countById(student.getStudentId()) > 0) {
            throw new BusinessException(400, "该学号已注册");
        }
        studentMapper.insert(student);
        return student;
    }

    /**
     * 管理员登录
     * @param account 账号
     * @param password 密码
     * @return 登录成功的管理员对象
     * @throws BusinessException 账号不存在或密码错误时抛出 401 异常
     */
    public Admin adminLogin(String account, String password) {
        Admin admin = adminMapper.selectByAccount(account);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        throw new BusinessException(401, "账号或密码错误");
    }
}

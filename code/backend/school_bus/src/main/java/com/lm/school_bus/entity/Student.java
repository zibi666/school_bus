package com.lm.school_bus.entity;

import lombok.Data;

/**
 * 学生实体类
 * 对应数据库表 student_info
 */
@Data
public class Student {
    /** 学号（主键，唯一标识） */
    private String studentId;
    
    /** 学生姓名 */
    private String name;
    
    /** 登录密码 */
    private String password;
    
    /** 所在位置或地址 */
    private String location;
}

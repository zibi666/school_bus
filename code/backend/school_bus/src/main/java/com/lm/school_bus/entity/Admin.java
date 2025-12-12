package com.lm.school_bus.entity;

import lombok.Data;

/**
 * 管理员实体类
 * 对应数据库表 admin_info
 */
@Data
public class Admin {
    /** 管理员 ID（主键，自增） */
    private Integer adminId;
    
    /** 管理员账号（用于登录） */
    private String account;
    
    /** 管理员密码 */
    private String password;
    
    /** 管理员姓名 */
    private String name;
}

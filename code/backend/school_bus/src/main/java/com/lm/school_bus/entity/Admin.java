package com.lm.school_bus.entity;

import lombok.Data;

@Data
public class Admin {
    private Integer adminId;
    private String account;
    private String password;
    private String name;
}

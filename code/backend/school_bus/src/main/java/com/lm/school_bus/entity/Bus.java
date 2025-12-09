package com.lm.school_bus.entity;

import lombok.Data;

@Data
public class Bus {
    private Integer busId;
    private String plateNumber;
    private String carType;
    private String driverName;
    private Boolean isActive = true; // 1-空闲, 0-使用中
    private Integer price; // 每小时价格（元）
}

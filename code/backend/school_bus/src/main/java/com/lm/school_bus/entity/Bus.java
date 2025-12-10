package com.lm.school_bus.entity;

import lombok.Data;

@Data
public class Bus {
    private Integer busId;
    private String plateNumber;
    private String carType;
    private String driverName;
    private String number; // 司机号码
    private Integer price; // 每小时价格（元）
}

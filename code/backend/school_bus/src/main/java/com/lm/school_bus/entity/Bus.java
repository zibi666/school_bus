package com.lm.school_bus.entity;

import lombok.Data;

/**
 * 车辆实体类
 * 对应数据库表 t_bus
 */
@Data
public class Bus {
    /** 车辆 ID（主键，自增） */
    private Integer busId;
    
    /** 车牌号（唯一标识，如 京A-12345） */
    private String plateNumber;
    
    /** 车型（如 大巴、中巴、商务车） */
    private String carType;
    
    /** 司机姓名 */
    private String driverName;
    
    /** 司机电话号码 */
    private String number;
    
    /** 每小时租车价格（单位：元） */
    private Integer price;
}

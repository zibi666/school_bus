package com.lm.school_bus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer orderId;
    private String studentId;
    private String destination;
    private String usageTime;
    private String requestedCarType;
    private Integer busId;
    private BigDecimal price;
    private String status = "审核中"; // 审核中/已通过/已拒绝
    private String rejectReason;
    private LocalDateTime createTime;
    
    // Transient fields for easier frontend display if needed
    private Bus bus;
}

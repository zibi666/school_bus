package com.lm.school_bus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer orderId;
    private String studentId;
    private String destination;
    private LocalDateTime startTime;  // 使用开始时间
    private LocalDateTime endTime;    // 使用结束时间
    private String requestedCarType;
    private Integer busId;
    private BigDecimal price;
    private String status = "审核中"; // 审核中/已通过/已拒绝
    private String rejectReason;
    private Boolean isPaid = false;  // 是否已支付
    
    // Transient fields for easier frontend display if needed
    private Bus bus;
}

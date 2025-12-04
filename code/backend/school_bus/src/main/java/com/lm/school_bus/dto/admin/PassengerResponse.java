package com.lm.school_bus.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PassengerResponse {
    private Long studentId;
    private String name;
    private Integer seatNumber;
}

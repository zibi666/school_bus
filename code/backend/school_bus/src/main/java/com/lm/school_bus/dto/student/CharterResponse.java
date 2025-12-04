package com.lm.school_bus.dto.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CharterResponse {
    private String inviteCode;
    private String plateNumber;
    private String driverName;
    private String driverPhone;
}

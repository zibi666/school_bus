package com.lm.school_bus.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverResponse {
    private String name;
    private String phone;
}

package com.lm.school_bus.dto.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileResponse {
    private Long studentId;
    private String name;
    private String gender;
    private String clazz;
}

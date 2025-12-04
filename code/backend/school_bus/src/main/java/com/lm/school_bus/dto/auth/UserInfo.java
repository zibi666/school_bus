package com.lm.school_bus.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserInfo {
    private String role;
    private String name;
    private Long studentId;
    private Integer adminId;
    private String clazz;
    private String gender;
}

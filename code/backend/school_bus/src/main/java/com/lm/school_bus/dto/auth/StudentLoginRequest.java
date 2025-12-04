package com.lm.school_bus.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentLoginRequest {

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "\\d+", message = "学号必须为数字")
    private String studentId;

    @NotBlank(message = "密码不能为空")
    private String password;
}

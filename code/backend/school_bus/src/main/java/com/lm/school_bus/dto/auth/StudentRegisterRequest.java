package com.lm.school_bus.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRegisterRequest {

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "\\d+", message = "学号必须为数字")
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotBlank(message = "班级不能为空")
    private String clazz;

    @NotBlank(message = "密码不能为空")
    private String password;
}

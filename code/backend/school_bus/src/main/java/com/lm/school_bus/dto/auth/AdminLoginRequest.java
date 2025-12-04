package com.lm.school_bus.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginRequest {

    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "\\d+", message = "管理员账号必须为数字")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}

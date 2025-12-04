package com.lm.school_bus.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest {

    @NotBlank(message = "司机姓名不能为空")
    private String name;

    @NotBlank(message = "司机电话不能为空")
    private String phone;
}

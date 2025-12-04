package com.lm.school_bus.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeDriverRequest {

    @NotBlank(message = "司机电话不能为空")
    private String driverPhone;
}

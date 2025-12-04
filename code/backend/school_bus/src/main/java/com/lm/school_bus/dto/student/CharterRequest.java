package com.lm.school_bus.dto.student;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CharterRequest {

    @NotNull(message = "日期不能为空")
    @FutureOrPresent(message = "日期不能早于今天")
    private LocalDate date;

    @NotBlank(message = "出发地不能为空")
    private String startLocation;

    @NotBlank(message = "目的地不能为空")
    private String endLocation;

    @NotBlank(message = "车型不能为空")
    private String vehicleType;
}

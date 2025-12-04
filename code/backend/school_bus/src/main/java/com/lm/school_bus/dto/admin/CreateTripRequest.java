package com.lm.school_bus.dto.admin;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTripRequest {

    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;

    @NotBlank(message = "车型不能为空")
    private String vehicleType;

    @NotNull(message = "用车日期不能为空")
    @FutureOrPresent(message = "日期不能早于今天")
    private LocalDate date;

    @NotBlank(message = "出发地不能为空")
    private String startLocation;

    @NotBlank(message = "目的地不能为空")
    private String endLocation;

    @NotNull(message = "座位数不能为空")
    @Min(value = 1, message = "座位数必须大于0")
    private Integer maxSeats;
}

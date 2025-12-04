package com.lm.school_bus.dto.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripResponse {
    private String plateNumber;
    private String vehicleType;
    private LocalDate date;
    private String startLocation;
    private String endLocation;
    private Integer seatNumber;
    private String driverName;
    private String driverPhone;
    private Double price;
    private String inviteCode;
}

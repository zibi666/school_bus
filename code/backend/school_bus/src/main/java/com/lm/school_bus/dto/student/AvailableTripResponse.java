package com.lm.school_bus.dto.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class AvailableTripResponse {
    private String plateNumber;
    private String vehicleType;
    private LocalDate date;
    private String startLocation;
    private String endLocation;
    private Integer maxSeats;
}

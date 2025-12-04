package com.lm.school_bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "bus_schedules")
public class BusSchedule {

    @Id
    @Column(name = "bus_id")
    private String busId;

    @Column(name = "bus_type", nullable = false)
    private String busType;

    @Column(name = "use_date", nullable = false)
    private LocalDate useDate;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "number", nullable = false)
    private Integer passengerCount;
}

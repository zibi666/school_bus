package com.lm.school_bus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "passenger_info")
public class PassengerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "bus_id", nullable = false)
    private String busId;

    @Column(name = "passenger_seat", nullable = false)
    private String passengerSeat;

    @Column(name = "passenger_number", nullable = false)
    private Long passengerNumber;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;
}

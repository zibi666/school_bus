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
@Table(name = "student_ticket_orders")
public class StudentTicketOrder {

    @Column(name = "bus_type", nullable = false)
    private String busType;

    @Id
    @Column(name = "bus_id")
    private String busId;

    @Column(name = "seat", nullable = false)
    private String seat;

    @Column(name = "use_date", nullable = false)
    private LocalDate useDate;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "number", nullable = false)
    private Integer numberOfPassengers;

    @Column(name = "driver", nullable = false)
    private String driverName;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "invite_code", nullable = false)
    private String inviteCode;
}

package com.lm.school_bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "driver_info")
public class Driver {

    @Column(name = "driver_name", nullable = false)
    private String name;

    @Id
    @Column(name = "driver_telephone")
    private String phone;
}

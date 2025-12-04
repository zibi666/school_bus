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
@Table(name = "manage_info")
public class Admin {

    @Id
    @Column(name = "manage_id")
    private Integer adminId;

    @Column(name = "manage_name", nullable = false)
    private String name;

    @Column(name = "manage_passwd", nullable = false)
    private String password;
}

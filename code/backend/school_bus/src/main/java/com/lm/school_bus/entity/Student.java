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
@Table(name = "student_info")
public class Student {

    @Id
    @Column(name = "stu_id")
    private Long studentId;

    @Column(name = "stu_name", nullable = false)
    private String name;

    @Column(name = "stu_sex", nullable = false)
    private String gender;

    @Column(name = "stu_class", nullable = false)
    private String clazz;

    @Column(name = "stu_passwd", nullable = false)
    private String password;
}

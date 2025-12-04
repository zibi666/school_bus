package com.lm.school_bus.repository;

import com.lm.school_bus.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(Long studentId);

    boolean existsByStudentId(Long studentId);
}

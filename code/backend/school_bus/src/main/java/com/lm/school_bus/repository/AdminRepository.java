package com.lm.school_bus.repository;

import com.lm.school_bus.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByAdminId(Integer adminId);

    boolean existsByAdminId(Integer adminId);
}

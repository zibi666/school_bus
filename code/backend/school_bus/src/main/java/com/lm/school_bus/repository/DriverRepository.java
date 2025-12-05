package com.lm.school_bus.repository;

import com.lm.school_bus.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {

	Optional<Driver> findFirstByName(String name);
}

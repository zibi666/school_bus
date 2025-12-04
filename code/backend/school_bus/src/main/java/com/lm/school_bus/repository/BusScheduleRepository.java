package com.lm.school_bus.repository;

import com.lm.school_bus.entity.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BusScheduleRepository extends JpaRepository<BusSchedule, String> {

    List<BusSchedule> findByUseDate(LocalDate useDate);
}

package com.lm.school_bus.repository;

import com.lm.school_bus.entity.PassengerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerInfoRepository extends JpaRepository<PassengerInfo, Long> {

    List<PassengerInfo> findByPassengerNumber(Long passengerNumber);

    List<PassengerInfo> findByBusIdOrderByPassengerSeatAsc(String busId);

    boolean existsByBusIdAndPassengerNumber(String busId, Long passengerNumber);

    Optional<PassengerInfo> findByBusIdAndPassengerNumber(String busId, Long passengerNumber);

    void deleteByBusId(String busId);
}

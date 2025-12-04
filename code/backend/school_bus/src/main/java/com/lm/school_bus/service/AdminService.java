package com.lm.school_bus.service;

import com.lm.school_bus.dto.admin.*;
import com.lm.school_bus.entity.BusSchedule;
import com.lm.school_bus.entity.Driver;
import com.lm.school_bus.entity.PassengerInfo;
import com.lm.school_bus.entity.StudentTicketOrder;
import com.lm.school_bus.exception.BadRequestException;
import com.lm.school_bus.exception.ResourceNotFoundException;
import com.lm.school_bus.repository.BusScheduleRepository;
import com.lm.school_bus.repository.DriverRepository;
import com.lm.school_bus.repository.PassengerInfoRepository;
import com.lm.school_bus.repository.StudentTicketOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final BusScheduleRepository busScheduleRepository;
    private final StudentTicketOrderRepository orderRepository;
    private final PassengerInfoRepository passengerInfoRepository;
    private final DriverRepository driverRepository;

    public AdminService(BusScheduleRepository busScheduleRepository,
                        StudentTicketOrderRepository orderRepository,
                        PassengerInfoRepository passengerInfoRepository,
                        DriverRepository driverRepository) {
        this.busScheduleRepository = busScheduleRepository;
        this.orderRepository = orderRepository;
        this.passengerInfoRepository = passengerInfoRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional(readOnly = true)
    public List<TripOverviewResponse> listTrips() {
        return busScheduleRepository.findAll().stream()
                .map(this::buildTripOverview)
                .sorted(Comparator.comparing(TripOverviewResponse::getDate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PassengerResponse> listPassengers(String plateNumber) {
        return passengerInfoRepository.findByBusIdOrderByPassengerSeatAsc(plateNumber).stream()
                .map(passenger -> PassengerResponse.builder()
                        .studentId(passenger.getPassengerNumber())
                        .name(passenger.getPassengerName())
                        .seatNumber(parseSeat(passenger.getPassengerSeat()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeDriver(String plateNumber, String driverPhone) {
        Objects.requireNonNull(plateNumber, "plateNumber不能为空");
        Objects.requireNonNull(driverPhone, "driverPhone不能为空");
        Driver driver = driverRepository.findById(driverPhone)
                .orElseThrow(() -> new ResourceNotFoundException("司机不存在"));
        StudentTicketOrder order = orderRepository.findById(plateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("车次不存在"));
        order.setDriverPhone(driver.getPhone());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> drivers() {
        return driverRepository.findAll().stream()
                .map(driver -> DriverResponse.builder()
                        .name(driver.getName())
                        .phone(driver.getPhone())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverResponse addDriver(DriverRequest request) {
        if (driverRepository.existsById(request.getPhone())) {
            throw new BadRequestException("该电话已存在司机");
        }
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setPhone(request.getPhone());
        driverRepository.save(driver);
        return DriverResponse.builder()
                .name(driver.getName())
                .phone(driver.getPhone())
                .build();
    }

    private TripOverviewResponse buildTripOverview(BusSchedule schedule) {
        StudentTicketOrder order = orderRepository.findById(schedule.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("车次" + schedule.getBusId() + "缺少订单信息"));
        Driver driver = driverRepository.findById(order.getDriverPhone()).orElse(null);
        return TripOverviewResponse.builder()
                .plateNumber(schedule.getBusId())
                .vehicleType(schedule.getBusType())
                .date(schedule.getUseDate())
                .startLocation(schedule.getOrigin())
                .endLocation(schedule.getDestination())
                .passengerCount(schedule.getPassengerCount())
                .driverName(driver != null ? driver.getName() : "-")
                .driverPhone(driver != null ? driver.getPhone() : order.getDriverPhone())
                .build();
    }

    private int parseSeat(String seat) {
        try {
            return Integer.parseInt(seat);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

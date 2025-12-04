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

    @Transactional
    public TripOverviewResponse createTrip(CreateTripRequest request) {
        String plateNumber = Objects.requireNonNull(request.getPlateNumber(), "车牌不能为空");
        if (busScheduleRepository.existsById(plateNumber)) {
            throw new BadRequestException("该车牌已存在车次");
        }
        BusSchedule schedule = new BusSchedule();
        schedule.setBusId(plateNumber);
        schedule.setBusType(request.getVehicleType());
        schedule.setUseDate(request.getDate());
        schedule.setOrigin(request.getStartLocation());
        schedule.setDestination(request.getEndLocation());
        schedule.setMaxNumber(request.getMaxSeats());
        schedule.setRemainingSeats(request.getMaxSeats());
        busScheduleRepository.save(schedule);
        return buildTripOverview(schedule);
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
        String phone = Objects.requireNonNull(request.getPhone(), "司机电话不能为空");
        if (driverRepository.existsById(phone)) {
            throw new BadRequestException("该电话已存在司机");
        }
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setPhone(phone);
        driverRepository.save(driver);
        return DriverResponse.builder()
                .name(driver.getName())
                .phone(driver.getPhone())
                .build();
    }

    private TripOverviewResponse buildTripOverview(BusSchedule schedule) {
        String busId = Objects.requireNonNull(schedule.getBusId());
        StudentTicketOrder order = orderRepository.findById(busId).orElse(null);
        Driver driver = null;
        String driverPhone = "-";
        int passengerCount = 0;
        if (order != null) {
            passengerCount = order.getNumberOfPassengers();
            driverPhone = order.getDriverPhone();
            if (driverPhone != null) {
                driver = driverRepository.findById(driverPhone).orElse(null);
            }
        }
        return TripOverviewResponse.builder()
                .plateNumber(schedule.getBusId())
                .vehicleType(schedule.getBusType())
                .date(schedule.getUseDate())
                .startLocation(schedule.getOrigin())
                .endLocation(schedule.getDestination())
            .passengerCount(passengerCount)
            .maxSeats(schedule.getMaxNumber())
            .remainingSeats(schedule.getRemainingSeats())
            .driverName(driver != null ? driver.getName() : "-")
            .driverPhone(driver != null ? driver.getPhone() : driverPhone)
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

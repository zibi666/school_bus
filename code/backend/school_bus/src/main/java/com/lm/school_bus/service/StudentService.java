package com.lm.school_bus.service;

import com.lm.school_bus.dto.student.*;
import com.lm.school_bus.entity.*;
import com.lm.school_bus.exception.BadRequestException;
import com.lm.school_bus.exception.ResourceNotFoundException;
import com.lm.school_bus.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final BusScheduleRepository busScheduleRepository;
    private final StudentTicketOrderRepository orderRepository;
    private final PassengerInfoRepository passengerInfoRepository;
    private final DriverRepository driverRepository;
    private final InviteCodeService inviteCodeService;

    private final SecureRandom random = new SecureRandom();

    private final double defaultPrice;

    public StudentService(StudentRepository studentRepository,
                          BusScheduleRepository busScheduleRepository,
                          StudentTicketOrderRepository orderRepository,
                          PassengerInfoRepository passengerInfoRepository,
                          DriverRepository driverRepository,
                          InviteCodeService inviteCodeService,
                          @Value("${app.charter.price.default:388.88}") double defaultPrice) {
        this.studentRepository = studentRepository;
        this.busScheduleRepository = busScheduleRepository;
        this.orderRepository = orderRepository;
        this.passengerInfoRepository = passengerInfoRepository;
        this.driverRepository = driverRepository;
        this.inviteCodeService = inviteCodeService;
        this.defaultPrice = defaultPrice;
    }


    @Transactional(readOnly = true)
    public List<AvailableTripResponse> listAvailableTrips() {
        return busScheduleRepository.findAll().stream()
                .filter(schedule -> Objects.equals(schedule.getRemainingSeats(), schedule.getMaxNumber()))
                .sorted(Comparator.comparing(BusSchedule::getUseDate))
                .map(schedule -> AvailableTripResponse.builder()
                        .plateNumber(schedule.getBusId())
                        .vehicleType(schedule.getBusType())
                        .date(schedule.getUseDate())
                        .startLocation(schedule.getOrigin())
                        .endLocation(schedule.getDestination())
                        .maxSeats(schedule.getMaxNumber())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public CharterResponse bookTrip(Long studentId, String busId) {
        Objects.requireNonNull(studentId, "studentId不能为空");
        Objects.requireNonNull(busId, "busId不能为空");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在"));
        BusSchedule schedule = busScheduleRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("车次不存在"));
        if (!Objects.equals(schedule.getRemainingSeats(), schedule.getMaxNumber())) {
            throw new BadRequestException("该车次已被其它同学选中");
        }
        Driver driver = pickDriver();
        String inviteCode = inviteCodeService.generateUniqueCode();

        StudentTicketOrder order = new StudentTicketOrder();
        order.setBusId(busId);
        order.setBusType(schedule.getBusType());
        order.setSeat("1");
        order.setUseDate(schedule.getUseDate());
        order.setOrigin(schedule.getOrigin());
        order.setDestination(schedule.getDestination());
        order.setNumberOfPassengers(1);
        order.setDriverPhone(driver.getPhone());
        order.setPrice(calculatePrice(schedule.getBusType()));
        order.setInviteCode(inviteCode);
        orderRepository.save(order);

        PassengerInfo passenger = new PassengerInfo();
        passenger.setBusId(busId);
        passenger.setPassengerSeat("1");
        passenger.setPassengerNumber(studentId);
        passenger.setPassengerName(student.getName());
        passengerInfoRepository.save(passenger);

        schedule.setRemainingSeats(Math.max(0, schedule.getRemainingSeats() - 1));
        busScheduleRepository.save(schedule);

        return CharterResponse.builder()
                .inviteCode(inviteCode)
                .plateNumber(busId)
                .driverName(driver.getName())
                .driverPhone(driver.getPhone())
                .build();
    }

    @Transactional
    public void join(Long studentId, JoinRequest request) {
        Objects.requireNonNull(studentId, "studentId不能为空");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在"));
        StudentTicketOrder order = orderRepository.findByInviteCode(request.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("邀请码无效"));
        String orderBusId = Objects.requireNonNull(order.getBusId(), "订单缺失车次信息");
        if (passengerInfoRepository.existsByBusIdAndPassengerNumber(orderBusId, studentId)) {
            throw new BadRequestException("已在该车次中");
        }
        BusSchedule schedule = busScheduleRepository.findById(orderBusId)
            .orElseThrow(() -> new ResourceNotFoundException("车次不存在"));
        if (schedule.getRemainingSeats() <= 0) {
            throw new BadRequestException("该车次座位已满");
        }
        int seatNumber = nextSeatNumber(orderBusId);

        PassengerInfo passenger = new PassengerInfo();
        passenger.setBusId(orderBusId);
        passenger.setPassengerSeat(String.valueOf(seatNumber));
        passenger.setPassengerNumber(studentId);
        passenger.setPassengerName(student.getName());
        passengerInfoRepository.save(passenger);

        schedule.setRemainingSeats(Math.max(0, schedule.getRemainingSeats() - 1));
        order.setNumberOfPassengers(order.getNumberOfPassengers() + 1);
        busScheduleRepository.save(schedule);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> listTrips(Long studentId) {
        Objects.requireNonNull(studentId, "studentId不能为空");
        List<PassengerInfo> passengers = passengerInfoRepository.findByPassengerNumber(studentId);
        return passengers.stream()
                .map(passenger -> buildTripResponse(passenger, passenger.getPassengerSeat()))
                .sorted(Comparator.comparing(TripResponse::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public void refund(Long studentId, String plateNumber) {
        Objects.requireNonNull(studentId, "studentId不能为空");
        Objects.requireNonNull(plateNumber, "plateNumber不能为空");
        PassengerInfo passenger = passengerInfoRepository.findByBusIdAndPassengerNumber(plateNumber, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应乘车记录"));
        BusSchedule schedule = busScheduleRepository.findById(plateNumber)
            .orElseThrow(() -> new ResourceNotFoundException("车次不存在"));
        StudentTicketOrder order = orderRepository.findById(plateNumber)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        int seatNumber = parseSeat(passenger.getPassengerSeat());
        if (seatNumber == 1) {
            passengerInfoRepository.deleteByBusId(plateNumber);
            orderRepository.delete(Objects.requireNonNull(order));
            schedule.setRemainingSeats(schedule.getMaxNumber());
            busScheduleRepository.save(schedule);
        } else {
            passengerInfoRepository.delete(passenger);
            schedule.setRemainingSeats(Math.min(schedule.getMaxNumber(), schedule.getRemainingSeats() + 1));
            order.setNumberOfPassengers(Math.max(0, order.getNumberOfPassengers() - 1));
            busScheduleRepository.save(schedule);
            orderRepository.save(order);
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse profile(Long studentId) {
        Objects.requireNonNull(studentId, "studentId不能为空");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在"));
        return ProfileResponse.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .gender(student.getGender())
                .clazz(student.getClazz())
                .build();
    }

    private int nextSeatNumber(String busId) {
        List<PassengerInfo> passengers = passengerInfoRepository.findByBusIdOrderByPassengerSeatAsc(busId);
        Set<Integer> seats = passengers.stream()
                .map(passenger -> parseSeat(passenger.getPassengerSeat()))
                .collect(Collectors.toSet());
        int seat = 1;
        while (seats.contains(seat)) {
            seat++;
        }
        return seat;
    }

    private int parseSeat(String seat) {
        try {
            return Integer.parseInt(seat);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private TripResponse buildTripResponse(PassengerInfo passenger, String seat) {
        String busId = Objects.requireNonNull(passenger.getBusId(), "车次编号缺失");
        BusSchedule schedule = busScheduleRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("车次不存在"));
        StudentTicketOrder order = orderRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        String driverPhone = Objects.requireNonNull(order.getDriverPhone(), "司机电话缺失");
        Driver driver = driverRepository.findById(driverPhone).orElse(null);
        boolean isOwner = parseSeat(seat) == 1;
        return TripResponse.builder()
                .plateNumber(busId)
                .vehicleType(schedule.getBusType())
                .date(schedule.getUseDate())
                .startLocation(schedule.getOrigin())
                .endLocation(schedule.getDestination())
                .seatNumber(parseSeat(seat))
                .driverName(driver != null ? driver.getName() : "-")
                .driverPhone(driver != null ? driver.getPhone() : order.getDriverPhone())
                .price(order.getPrice())
                .inviteCode(isOwner ? order.getInviteCode() : null)
                .build();
    }

    private Driver pickDriver() {
        List<Driver> drivers = driverRepository.findAll();
        if (drivers.isEmpty()) {
            throw new BadRequestException("请先在后台新增司机信息");
        }
        return drivers.get(random.nextInt(drivers.size()));
    }

    private double calculatePrice(String vehicleType) {
        if (vehicleType == null) {
            return defaultPrice;
        }
        return switch (vehicleType) {
            case "客车", "大巴" -> defaultPrice + 100;
            case "商务车", "SUV" -> defaultPrice - 50;
            default -> defaultPrice;
        };
    }
}

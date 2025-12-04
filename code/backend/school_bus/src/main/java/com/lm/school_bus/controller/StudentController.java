package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.dto.student.*;
import com.lm.school_bus.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private static final String STUDENT_ID_HEADER = "X-Student-Id";

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/trips/available")
    public ApiResponse<?> availableTrips() {
        return ApiResponse.success(studentService.listAvailableTrips());
    }

    @PostMapping("/trips/{busId}/book")
    public ApiResponse<CharterResponse> bookTrip(@RequestHeader(STUDENT_ID_HEADER) Long studentId,
                                                 @PathVariable String busId) {
        return ApiResponse.success(studentService.bookTrip(studentId, busId));
    }

    @PostMapping("/join")
    public ApiResponse<Void> join(@RequestHeader(STUDENT_ID_HEADER) Long studentId,
                                  @Valid @RequestBody JoinRequest request) {
        studentService.join(studentId, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/trips")
    public ApiResponse<?> trips(@RequestHeader(STUDENT_ID_HEADER) Long studentId) {
        return ApiResponse.success(studentService.listTrips(studentId));
    }

    @PostMapping("/refund/{plateNumber}")
    public ApiResponse<Void> refund(@RequestHeader(STUDENT_ID_HEADER) Long studentId,
                                    @PathVariable String plateNumber) {
        studentService.refund(studentId, plateNumber);
        return ApiResponse.success(null);
    }

    @GetMapping("/profile")
    public ApiResponse<ProfileResponse> profile(@RequestHeader(STUDENT_ID_HEADER) Long studentId) {
        return ApiResponse.success(studentService.profile(studentId));
    }
}

package com.lm.school_bus.controller;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.dto.admin.ChangeDriverRequest;
import com.lm.school_bus.dto.admin.CreateTripRequest;
import com.lm.school_bus.dto.admin.DriverRequest;
import com.lm.school_bus.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final String ADMIN_ID_HEADER = "X-Admin-Id";

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/trips")
    public ApiResponse<?> createTrip(@RequestHeader(ADMIN_ID_HEADER) Integer adminId,
                                     @Valid @RequestBody CreateTripRequest request) {
        return ApiResponse.success(adminService.createTrip(request));
    }

    @GetMapping("/trips")
    public ApiResponse<?> trips(@RequestHeader(ADMIN_ID_HEADER) Integer adminId) {
        return ApiResponse.success(adminService.listTrips());
    }

    @GetMapping("/trips/{plateNumber}/passengers")
    public ApiResponse<?> passengers(@RequestHeader(ADMIN_ID_HEADER) Integer adminId,
                                     @PathVariable String plateNumber) {
        return ApiResponse.success(adminService.listPassengers(plateNumber));
    }

    @PutMapping("/trips/{plateNumber}/driver")
    public ApiResponse<Void> changeDriver(@PathVariable String plateNumber,
                                          @RequestHeader(ADMIN_ID_HEADER) Integer adminId,
                                          @Valid @RequestBody ChangeDriverRequest request) {
        adminService.changeDriver(plateNumber, request.getDriverPhone());
        return ApiResponse.success(null);
    }

    @GetMapping("/drivers")
    public ApiResponse<?> drivers(@RequestHeader(ADMIN_ID_HEADER) Integer adminId) {
        return ApiResponse.success(adminService.drivers());
    }

    @PostMapping("/drivers")
    public ApiResponse<?> addDriver(@RequestHeader(ADMIN_ID_HEADER) Integer adminId,
                                    @Valid @RequestBody DriverRequest request) {
        return ApiResponse.success(adminService.addDriver(request));
    }
}

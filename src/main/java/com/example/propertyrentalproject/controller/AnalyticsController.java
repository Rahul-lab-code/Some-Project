package com.example.propertyrentalproject.controller;
 
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.Map;
 
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Analytics", description = "Admin dashboard and analytics")
public class AnalyticsController {
 
    private final AnalyticsService analyticsService;
 
    @GetMapping("/dashboard")
    @Operation(summary = "Get full admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard data",
                analyticsService.getDashboard()));
    }
 
    @GetMapping("/users")
    @Operation(summary = "Get user statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        return ResponseEntity.ok(ApiResponse.success("User stats",
                analyticsService.getUserStats()));
    }
 
    @GetMapping("/properties")
    @Operation(summary = "Get property statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPropertyStats() {
        return ResponseEntity.ok(ApiResponse.success("Property stats",
                analyticsService.getPropertyStats()));
    }
 
    @GetMapping("/bookings")
    @Operation(summary = "Get booking statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookingStats() {
        return ResponseEntity.ok(ApiResponse.success("Booking stats",
                analyticsService.getBookingStats()));
    }
 
    @GetMapping("/revenue")
    @Operation(summary = "Get revenue statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueStats() {
        return ResponseEntity.ok(ApiResponse.success("Revenue stats",
                analyticsService.getRevenueStats()));
    }
 
    @GetMapping("/disputes")
    @Operation(summary = "Get dispute statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDisputeStats() {
        return ResponseEntity.ok(ApiResponse.success("Dispute stats",
                analyticsService.getDisputeStats()));
    }
 
    @GetMapping("/maintenance")
    @Operation(summary = "Get maintenance statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMaintenanceStats() {
        return ResponseEntity.ok(ApiResponse.success("Maintenance stats",
                analyticsService.getMaintenanceStats()));
    }
}
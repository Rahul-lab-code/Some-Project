package com.example.propertyrentalproject.controller;
 
import com.example.propertyrentalproject.dto.requests.MaintenanceRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.MaintenanceResponse;
import com.example.propertyrentalproject.enums.MaintenanceStatus;
import com.example.propertyrentalproject.service.MaintenanceService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Maintenance and damage ticket management")
public class MaintenanceController {
 
    private final MaintenanceService maintenanceService;
    private final SecurityUtil securityUtil;
 
    // ── Host or Guest endpoints ────────────────────────────────
 
    @PostMapping
    @Operation(summary = "Create a maintenance ticket (HOST or GUEST)")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> createTicket(
            @Valid @RequestBody MaintenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket created",
                        maintenanceService.createTicket(
                                securityUtil.getCurrentUserId(), request)));
    }
 
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID (HOST or reporter)")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> getTicket(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Ticket fetched",
                maintenanceService.getTicket(
                        id, securityUtil.getCurrentUserId())));
    }
 
    @GetMapping("/my-tickets")
    @Operation(summary = "Get tickets I reported")
    public ResponseEntity<ApiResponse<List<MaintenanceResponse>>> getMyTickets() {
        return ResponseEntity.ok(ApiResponse.success("My tickets",
                maintenanceService.getMyTickets(
                        securityUtil.getCurrentUserId())));
    }
 
    // ── Host endpoints ─────────────────────────────────────────
 
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get all tickets for a property (HOST)")
    public ResponseEntity<ApiResponse<List<MaintenanceResponse>>> getTicketsByProperty(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(ApiResponse.success("Property tickets",
                maintenanceService.getTicketsByProperty(
                        propertyId, securityUtil.getCurrentUserId())));
    }
 
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Update ticket status (HOST)")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam MaintenanceStatus status,
            @RequestParam(required = false) String resolutionNote) {
        return ResponseEntity.ok(ApiResponse.success("Ticket status updated",
                maintenanceService.updateTicketStatus(
                        securityUtil.getCurrentUserId(),
                        id, status, resolutionNote)));
    }
 
    // ── Admin endpoints ────────────────────────────────────────
 
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tickets (ADMIN)")
    public ResponseEntity<ApiResponse<List<MaintenanceResponse>>> getAllTickets() {
        return ResponseEntity.ok(ApiResponse.success("All tickets",
                maintenanceService.getAllTickets()));
    }
 
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tickets by status (ADMIN)")
    public ResponseEntity<ApiResponse<List<MaintenanceResponse>>> getTicketsByStatus(
            @PathVariable MaintenanceStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Tickets by status",
                maintenanceService.getTicketsByStatus(status)));
    }
}
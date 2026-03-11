package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.DisputeRequest;
import com.example.propertyrentalproject.dto.requests.ResolveDisputeRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.DisputeResponse;
import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.service.DisputeService;
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
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@Tag(name = "Disputes", description = "Dispute management")
public class DisputeController {

    private final DisputeService disputeService;
    private final SecurityUtil securityUtil;

    // ── Guest or Host endpoints ────────────────────────────────

    @PostMapping
    @Operation(summary = "Raise a dispute (GUEST or HOST)")
    public ResponseEntity<ApiResponse<DisputeResponse>> raiseDispute(
            @Valid @RequestBody DisputeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dispute raised",
                        disputeService.raiseDispute(
                                securityUtil.getCurrentUserId(), request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dispute by ID (GUEST or HOST)")
    public ResponseEntity<ApiResponse<DisputeResponse>> getDispute(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Dispute fetched",
                disputeService.getDispute(
                        id, securityUtil.getCurrentUserId())));
    }

    @GetMapping("/my-disputes")
    @Operation(summary = "Get my disputes (GUEST or HOST)")
    public ResponseEntity<ApiResponse<List<DisputeResponse>>> getMyDisputes() {
        return ResponseEntity.ok(ApiResponse.success("My disputes",
                disputeService.getMyDisputes(
                        securityUtil.getCurrentUserId())));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get disputes for a booking (GUEST or HOST)")
    public ResponseEntity<ApiResponse<List<DisputeResponse>>> getDisputesByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Booking disputes",
                disputeService.getDisputesByBooking(
                        bookingId, securityUtil.getCurrentUserId())));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all disputes (ADMIN)")
    public ResponseEntity<ApiResponse<List<DisputeResponse>>> getAllDisputes() {
        return ResponseEntity.ok(ApiResponse.success("All disputes",
                disputeService.getAllDisputes()));
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get disputes by status (ADMIN)")
    public ResponseEntity<ApiResponse<List<DisputeResponse>>> getDisputesByStatus(
            @PathVariable DisputeStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Disputes by status",
                disputeService.getDisputesByStatus(status)));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update dispute status (ADMIN)")
    public ResponseEntity<ApiResponse<DisputeResponse>> updateDisputeStatus(
            @PathVariable Long id,
            @RequestParam DisputeStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Dispute status updated",
                disputeService.updateDisputeStatus(
                        securityUtil.getCurrentUserId(), id, status)));
    }

    @PostMapping("/admin/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolve a dispute (ADMIN)")
    public ResponseEntity<ApiResponse<DisputeResponse>> resolveDispute(
            @PathVariable Long id,
            @Valid @RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dispute resolved",
                disputeService.resolveDispute(
                        securityUtil.getCurrentUserId(), id, request)));
    }

    @PostMapping("/admin/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close a dispute (ADMIN)")
    public ResponseEntity<ApiResponse<DisputeResponse>> closeDispute(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Dispute closed",
                disputeService.closeDispute(
                        securityUtil.getCurrentUserId(), id)));
    }
}
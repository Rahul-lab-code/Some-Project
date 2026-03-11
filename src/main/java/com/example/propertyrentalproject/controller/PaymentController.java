package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.PaymentRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.EscrowResponse;
import com.example.propertyrentalproject.dto.responses.PaymentResponse;
import com.example.propertyrentalproject.service.PaymentService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment and escrow management")
public class PaymentController {

    private final PaymentService paymentService;
    private final SecurityUtil securityUtil;

    // ── Guest endpoints ────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Pay for a confirmed booking (GUEST)")
    public ResponseEntity<ApiResponse<PaymentResponse>> payForBooking(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment successful",
                        paymentService.payForBooking(
                                securityUtil.getCurrentUserId(), request)));
    }

    @GetMapping("/my-payments")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Get my payment history (GUEST)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments() {
        return ResponseEntity.ok(ApiResponse.success("Payment history",
                paymentService.getMyPayments(
                        securityUtil.getCurrentUserId())));
    }

    // ── Shared endpoints ───────────────────────────────────────

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payments for a booking (GUEST or HOST)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Booking payments",
                paymentService.getPaymentsByBooking(
                        bookingId, securityUtil.getCurrentUserId())));
    }

    @GetMapping("/booking/{bookingId}/escrow")
    @Operation(summary = "Get escrow details for a booking (GUEST or HOST)")
    public ResponseEntity<ApiResponse<EscrowResponse>> getEscrow(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Escrow details",
                paymentService.getEscrowByBooking(bookingId)));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments (ADMIN)")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success("All payments",
                paymentService.getAllPayments()));
    }

    @PostMapping("/admin/booking/{bookingId}/release-escrow")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Release escrow to host (ADMIN)")
    public ResponseEntity<ApiResponse<EscrowResponse>> releaseEscrow(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Escrow released",
                paymentService.releaseEscrow(
                        securityUtil.getCurrentUserId(), bookingId)));
    }

    @PostMapping("/admin/booking/{bookingId}/deduct-escrow")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deduct from escrow for damages (ADMIN)")
    public ResponseEntity<ApiResponse<EscrowResponse>> deductFromEscrow(
            @PathVariable Long bookingId,
            @RequestParam BigDecimal amount,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success("Escrow deducted",
                paymentService.deductFromEscrow(
                        securityUtil.getCurrentUserId(),
                        bookingId, amount, reason)));
    }

    @PostMapping("/admin/booking/{bookingId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund a booking payment (ADMIN)")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Refund processed",
                paymentService.refundBooking(
                        securityUtil.getCurrentUserId(), bookingId)));
    }
}
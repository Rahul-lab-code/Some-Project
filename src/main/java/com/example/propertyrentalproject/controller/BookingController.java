package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.CreateBookingRequest;
import com.example.propertyrentalproject.dto.requests.UpdateBookingStatusRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.BookingResponse;
import com.example.propertyrentalproject.service.BookingService;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking and reservation management")
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtil securityUtil;

    // ── Guest endpoints ────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Create a booking (GUEST)")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created",
                        bookingService.createBooking(
                                securityUtil.getCurrentUserId(), request)));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Get my bookings as guest (GUEST)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookingsAsGuest() {
        return ResponseEntity.ok(ApiResponse.success("My bookings",
                bookingService.getMyBookingsAsGuest(
                        securityUtil.getCurrentUserId())));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Cancel a booking (GUEST)")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled",
                bookingService.cancelBooking(
                        securityUtil.getCurrentUserId(), id, reason)));
    }

    // ── Host endpoints ─────────────────────────────────────────

    @GetMapping("/host-bookings")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get bookings for my properties (HOST)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookingsAsHost() {
        return ResponseEntity.ok(ApiResponse.success("Host bookings",
                bookingService.getMyBookingsAsHost(
                        securityUtil.getCurrentUserId())));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Update booking status (HOST)")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Booking status updated",
                bookingService.updateBookingStatus(
                        securityUtil.getCurrentUserId(), id, request)));
    }

    // ── Shared endpoints ───────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID (GUEST or HOST)")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Booking fetched",
                bookingService.getBooking(
                        id, securityUtil.getCurrentUserId())));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all bookings (ADMIN)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success("All bookings",
                bookingService.getAllBookings()));
    }
}
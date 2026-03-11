package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.BlockCalendarRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.CalendarResponse;
import com.example.propertyrentalproject.service.CalendarService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Property availability calendar management")
public class CalendarController {

    private final CalendarService calendarService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{propertyId}/block")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Block dates on calendar (HOST)")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> blockDates(
            @PathVariable Long propertyId,
            @Valid @RequestBody BlockCalendarRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dates blocked",
                calendarService.blockDates(
                        securityUtil.getCurrentUserId(), propertyId, request)));
    }

    @DeleteMapping("/{propertyId}/unblock")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Unblock a date on calendar (HOST)")
    public ResponseEntity<ApiResponse<String>> unblockDate(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                calendarService.unblockDate(
                        securityUtil.getCurrentUserId(), propertyId, date)));
    }

    @GetMapping("/{propertyId}")
    @Operation(summary = "Get all blocked dates for a property")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getBlockedDates(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(ApiResponse.success("Blocked dates",
                calendarService.getBlockedDates(propertyId)));
    }

    @GetMapping("/{propertyId}/range")
    @Operation(summary = "Get blocked dates in a date range")
    public ResponseEntity<ApiResponse<List<CalendarResponse>>> getBlockedDatesInRange(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success("Blocked dates in range",
                calendarService.getBlockedDatesInRange(propertyId, start, end)));
    }
}
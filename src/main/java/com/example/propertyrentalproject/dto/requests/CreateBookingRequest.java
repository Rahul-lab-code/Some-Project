package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in future")
    private LocalDate checkOutDate;

    @NotNull(message = "Guests count is required")
    @Min(value = 1, message = "At least 1 guest required")
    private Integer guestsCount;
}
package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BlockCalendarRequest {

    @NotNull(message = "Dates are required")
    private List<LocalDate> dates;

    private String reason;
}
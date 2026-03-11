package com.example.propertyrentalproject.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarResponse {

    private Long id;
    private Long propertyId;
    private LocalDate blockedDate;
    private String reason;
    private Long bookingId;
}
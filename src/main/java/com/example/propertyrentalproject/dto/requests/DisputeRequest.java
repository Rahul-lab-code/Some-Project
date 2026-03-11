package com.example.propertyrentalproject.dto.requests;

import com.example.propertyrentalproject.enums.DisputeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DisputeRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Dispute type is required")
    private DisputeType type;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> evidenceUrls;
}
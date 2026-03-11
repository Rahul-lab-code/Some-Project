package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MaintenanceRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    private Long bookingId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> photoUrls;
}
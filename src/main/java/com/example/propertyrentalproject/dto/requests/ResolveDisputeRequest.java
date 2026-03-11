package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveDisputeRequest {

    @NotBlank(message = "Resolution is required")
    private String resolution;
}
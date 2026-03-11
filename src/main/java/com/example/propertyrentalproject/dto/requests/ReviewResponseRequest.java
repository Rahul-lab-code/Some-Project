package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewResponseRequest {

    @NotBlank(message = "Response is required")
    private String response;
}
package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReviewRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Reviewee ID is required")
    private Long revieweeId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private BigDecimal rating;

    @DecimalMin(value = "1.0") @DecimalMax(value = "5.0")
    private BigDecimal cleanliness;

    @DecimalMin(value = "1.0") @DecimalMax(value = "5.0")
    private BigDecimal communication;

    @DecimalMin(value = "1.0") @DecimalMax(value = "5.0")
    private BigDecimal accuracy;

    @DecimalMin(value = "1.0") @DecimalMax(value = "5.0")
    private BigDecimal value;

    @DecimalMin(value = "1.0") @DecimalMax(value = "5.0")
    private BigDecimal ruleCompliance;

    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
    private String comment;
}
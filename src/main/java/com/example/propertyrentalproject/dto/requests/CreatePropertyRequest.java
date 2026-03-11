package com.example.propertyrentalproject.dto.requests;

import com.example.propertyrentalproject.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreatePropertyRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotBlank(message = "Address is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private String country = "India";

    @NotNull(message = "Bedrooms count is required")
    @Min(value = 0, message = "Bedrooms cannot be negative")
    private Integer bedrooms;

    @NotNull(message = "Bathrooms count is required")
    @Min(value = 0, message = "Bathrooms cannot be negative")
    private Integer bathrooms;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String houseRules;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    private BigDecimal weeklyPrice;
    private BigDecimal monthlyPrice;
    private BigDecimal cleaningFee;
    private BigDecimal securityDeposit;

    private String currency = "INR";

    private BookingMode bookingMode = BookingMode.INSTANT;
    private CancellationPolicy cancellationPolicy = CancellationPolicy.MODERATE;

    @Min(value = 1, message = "Minimum stay must be at least 1 night")
    private Integer minStayNights = 1;

    @Max(value = 365, message = "Maximum stay cannot exceed 365 nights")
    private Integer maxStayNights = 365;

    private List<String> imageUrls;
}
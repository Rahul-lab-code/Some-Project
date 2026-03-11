package com.example.propertyrentalproject.dto.requests;

import com.example.propertyrentalproject.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdatePropertyRequest {

    private String title;
    private String description;
    private PropertyType propertyType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer capacity;
    private String houseRules;
    private BigDecimal basePrice;
    private BigDecimal weeklyPrice;
    private BigDecimal monthlyPrice;
    private BigDecimal cleaningFee;
    private BigDecimal securityDeposit;
    private String currency;
    private BookingMode bookingMode;
    private CancellationPolicy cancellationPolicy;
    private Integer minStayNights;
    private Integer maxStayNights;
    private List<String> imageUrls;
}
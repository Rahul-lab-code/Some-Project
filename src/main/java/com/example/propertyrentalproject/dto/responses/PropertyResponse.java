package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyResponse {

    private Long id;
    private Long hostId;
    private String hostName;
    private boolean hostVerified;
    private String title;
    private String description;
    private PropertyType propertyType;
    private PropertyStatus status;
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
    private LocalDateTime createdAt;
}
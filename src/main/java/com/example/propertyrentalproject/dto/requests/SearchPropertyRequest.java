package com.example.propertyrentalproject.dto.requests;

import com.example.propertyrentalproject.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SearchPropertyRequest {

    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guests;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private PropertyType propertyType;
}
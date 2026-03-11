package com.example.propertyrentalproject.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {

    private Long id;
    private Long bookingId;
    private Long reviewerId;
    private String reviewerName;
    private Long revieweeId;
    private String revieweeName;
    private BigDecimal rating;
    private BigDecimal cleanliness;
    private BigDecimal communication;
    private BigDecimal accuracy;
    private BigDecimal value;
    private BigDecimal ruleCompliance;
    private String comment;
    private String response;
    private LocalDateTime createdAt;
}
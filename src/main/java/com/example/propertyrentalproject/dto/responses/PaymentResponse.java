package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.PaymentStatus;
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
public class PaymentResponse {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String transactionType;
    private LocalDateTime createdAt;
}
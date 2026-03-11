package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.enums.CancellationPolicy;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {

    private Long id;
    private Long guestId;
    private String guestName;
    private Long propertyId;
    private String propertyTitle;
    private Long hostId;
    private String hostName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestsCount;
    private BookingStatus status;
    private CancellationPolicy cancellationPolicy;
    private BigDecimal baseAmount;
    private BigDecimal cleaningFee;
    private BigDecimal serviceFee;
    private BigDecimal taxAmount;
    private BigDecimal securityDeposit;
    private BigDecimal totalAmount;
    private String checkInCode;
    private LocalDateTime createdAt;
}
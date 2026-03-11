package com.example.propertyrentalproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "escrow_deposits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EscrowDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status = "HELD"; // HELD, RELEASED, PARTIALLY_DEDUCTED, FORFEITED

    @Column(precision = 10, scale = 2)
    private BigDecimal deductedAmount;

    private String deductionReason;
    private LocalDateTime releasedAt;
}
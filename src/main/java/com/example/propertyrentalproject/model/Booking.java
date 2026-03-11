package com.example.propertyrentalproject.model;

import com.example.propertyrentalproject.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    private Integer guestsCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private CancellationPolicy cancellationPolicy;

    // Pricing snapshot
    @Column(precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal cleaningFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal serviceFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private String checkInCode;

    // Cancellation
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private String cancelledBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
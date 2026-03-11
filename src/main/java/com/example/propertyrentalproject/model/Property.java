package com.example.propertyrentalproject.model;

import com.example.propertyrentalproject.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.DRAFT;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country = "India";

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String houseRules;

    // Pricing
    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal weeklyPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal cleaningFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private BookingMode bookingMode = BookingMode.INSTANT;

    @Enumerated(EnumType.STRING)
    private CancellationPolicy cancellationPolicy = CancellationPolicy.MODERATE;

    private Integer minStayNights = 1;
    private Integer maxStayNights = 365;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropertyImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
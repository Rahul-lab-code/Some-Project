package com.example.propertyrentalproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(precision = 3, scale = 1)
    private BigDecimal cleanliness;

    @Column(precision = 3, scale = 1)
    private BigDecimal communication;

    @Column(precision = 3, scale = 1)
    private BigDecimal accuracy;

    @Column(precision = 3, scale = 1)
    private BigDecimal value;

    @Column(precision = 3, scale = 1)
    private BigDecimal ruleCompliance;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String response;

    private LocalDateTime respondedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
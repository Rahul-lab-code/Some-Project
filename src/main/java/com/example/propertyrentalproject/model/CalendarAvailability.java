package com.example.propertyrentalproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "calendar_availability",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"property_id", "blocked_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private LocalDate blockedDate;

    private String reason;
    private Long bookingId;
}
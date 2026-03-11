package com.example.propertyrentalproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private String imageUrl;

    private boolean isPrimary = false;
    private Integer sortOrder = 0;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
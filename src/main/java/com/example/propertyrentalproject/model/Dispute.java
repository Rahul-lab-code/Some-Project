package com.example.propertyrentalproject.model;

import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.enums.DisputeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disputes")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @Enumerated(EnumType.STRING)
    private DisputeType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long resolvedBy;
    private String resolution;
    private LocalDateTime resolvedAt;

    @ElementCollection
    @CollectionTable(name = "dispute_evidence_urls",
            joinColumns = @JoinColumn(name = "dispute_id"))
    @Column(name = "url")
    private List<String> evidenceUrls;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
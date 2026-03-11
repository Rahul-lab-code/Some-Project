package com.example.propertyrentalproject.model;

import com.example.propertyrentalproject.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_verifications")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class KYCVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Government ID
    private String govtIdType;       // AADHAAR / PASSPORT / DRIVING_LICENSE
    private String govtIdNumber;
    private String govtIdDocumentUrl;

    // PAN Card
    private String panNumber;
    private String panDocumentUrl;

    // Bank Details
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankAccountHolderName;
    private String cancelledChequeUrl;

    // Background check
    @Builder.Default
    private boolean backgroundCheckPassed = false;
    private String backgroundCheckNote;
    private LocalDateTime backgroundCheckDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private KycStatus status = KycStatus.NOT_SUBMITTED;

    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime submittedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

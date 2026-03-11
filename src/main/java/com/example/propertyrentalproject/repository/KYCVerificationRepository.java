package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.enums.KycStatus;
import com.example.propertyrentalproject.model.KYCVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KYCVerificationRepository extends JpaRepository<KYCVerification,Long> {
    Optional<KYCVerification> findByUserId(Long userId);
    List<KYCVerification> findByStatus(KycStatus status);
    boolean existsByUserId(Long userId);
}

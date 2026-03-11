package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.KycRequest;
import com.example.propertyrentalproject.dto.requests.UpdateProfileRequest;
import com.example.propertyrentalproject.dto.responses.UserResponse;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.KYCVerification;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.enums.KycStatus;
import com.example.propertyrentalproject.enums.RoleType;
import com.example.propertyrentalproject.repository.KYCVerificationRepository;
import com.example.propertyrentalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KYCVerificationRepository kycRepository;

    // ── Get profile ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return toResponse(findById(userId));
    }

    // ── Update profile ─────────────────────────────────────────
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findById(userId);

        if (req.getFirstName()    != null) user.setFirstName(req.getFirstName());
        if (req.getLastName()     != null) user.setLastName(req.getLastName());
        if (req.getProfilePhoto() != null) user.setProfilePhoto(req.getProfilePhoto());
        if (req.getPhone()        != null) {
            if (userRepository.existsByPhone(req.getPhone())
                    && !req.getPhone().equals(user.getPhone()))
                throw new IllegalArgumentException("Phone already in use");
            user.setPhone(req.getPhone());
        }
        return toResponse(userRepository.save(user));
    }

    // ── Submit KYC ─────────────────────────────────────────────
    @Transactional
    public String submitKyc(Long userId, KycRequest req) {
        User user = findById(userId);

        if (!RoleType.HOST.equals(user.getRole()))
            throw new UnauthorizedException("Only hosts can submit KYC");

        KYCVerification kyc = kycRepository.findByUserId(userId)
                .orElse(KYCVerification.builder().user(user).build());

        if (KycStatus.APPROVED.equals(kyc.getStatus()))
            throw new IllegalStateException("KYC already approved");

        kyc.setGovtIdType(req.getGovtIdType());
        kyc.setGovtIdNumber(req.getGovtIdNumber());
        kyc.setGovtIdDocumentUrl(req.getGovtIdDocumentUrl());
        kyc.setPanNumber(req.getPanNumber());
        kyc.setPanDocumentUrl(req.getPanDocumentUrl());
        kyc.setBankAccountNumber(req.getBankAccountNumber());
        kyc.setBankIfscCode(req.getBankIfscCode());
        kyc.setBankName(req.getBankName());
        kyc.setBankAccountHolderName(req.getBankAccountHolderName());
        kyc.setCancelledChequeUrl(req.getCancelledChequeUrl());
        kyc.setStatus(KycStatus.PENDING_REVIEW);

        kycRepository.save(kyc);
        user.setKycStatus(KycStatus.PENDING_REVIEW);
        userRepository.save(user);

        log.info("KYC submitted for host: {}", userId);
        return "KYC submitted successfully. Under review.";
    }

    // ── Admin: Approve KYC ─────────────────────────────────────
    @Transactional
    public String approveKyc(Long userId, Long adminId) {
        KYCVerification kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "KYC not found for user: " + userId));

        kyc.setStatus(KycStatus.APPROVED);
        kyc.setReviewedBy(adminId);
        kyc.setReviewedAt(LocalDateTime.now());
        kycRepository.save(kyc);

        User user = findById(userId);
        user.setKycStatus(KycStatus.APPROVED);
        user.setVerificationBadge(true);
        userRepository.save(user);

        log.info("KYC approved for user {} by admin {}", userId, adminId);
        return "KYC approved. Verification badge granted.";
    }

    // ── Admin: Reject KYC ──────────────────────────────────────
    @Transactional
    public String rejectKyc(Long userId, Long adminId, String reason) {
        KYCVerification kyc = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "KYC not found for user: " + userId));

        kyc.setStatus(KycStatus.REJECTED);
        kyc.setReviewedBy(adminId);
        kyc.setReviewedAt(LocalDateTime.now());
        kyc.setRejectionReason(reason);
        kycRepository.save(kyc);

        User user = findById(userId);
        user.setKycStatus(KycStatus.REJECTED);
        userRepository.save(user);

        log.info("KYC rejected for user {} by admin {}. Reason: {}", userId, adminId, reason);
        return "KYC rejected.";
    }

    // ── Admin: Deactivate user ─────────────────────────────────
    @Transactional
    public String deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", userId);
        return "User deactivated successfully";
    }

    // ── Admin: Get pending KYC users ───────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponse> getPendingKycUsers() {
        return userRepository
                .findByRoleAndKycStatus(RoleType.HOST, KycStatus.PENDING_REVIEW)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Update trust score (called by ReviewService) ───────────
    @Transactional
    public void updateTrustScore(Long userId, BigDecimal newRating) {
        User user = findById(userId);
        int total = user.getTotalReviews();
        BigDecimal current = user.getTrustScore();

        BigDecimal updated = current.multiply(BigDecimal.valueOf(total))
                .add(newRating)
                .divide(BigDecimal.valueOf(total + 1), 2, RoundingMode.HALF_UP);

        user.setTrustScore(updated);
        user.setTotalReviews(total + 1);
        userRepository.save(user);
    }

    // ── Admin: Search users ────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchUsers(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + id));
    }

    public UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .phone(u.getPhone())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .fullName(u.getFullName())
                .profilePhoto(u.getProfilePhoto())
                .role(u.getRole())
                .emailVerified(u.isEmailVerified())
                .phoneVerified(u.isPhoneVerified())
                .verificationBadge(u.isVerificationBadge())
                .trustScore(u.getTrustScore())
                .totalReviews(u.getTotalReviews())
                .kycStatus(u.getKycStatus())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
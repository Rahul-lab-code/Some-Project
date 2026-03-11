package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.KycStatus;
import com.example.propertyrentalproject.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profilePhoto;
    private RoleType role;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean verificationBadge;
    private BigDecimal trustScore;
    private int totalReviews;
    private KycStatus kycStatus;
    private LocalDateTime createdAt;
}

package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long userId;
    private String email;
    private String fullName;
    private RoleType role;
    private boolean emailVerified;
    private boolean verificationBadge;
}

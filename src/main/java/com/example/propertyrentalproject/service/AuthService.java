package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.LoginRequest;
import com.example.propertyrentalproject.dto.requests.RegisterRequest;
import com.example.propertyrentalproject.dto.responses.AuthResponse;
import com.example.propertyrentalproject.enums.RoleType;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.UserRepository;
import com.example.propertyrentalproject.security.JwtService;
import com.example.propertyrentalproject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ── Register ───────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        if (userRepository.existsByPhone(request.getPhone()))
            throw new IllegalArgumentException("Phone already registered");

        if (RoleType.ADMIN.equals(request.getRole()))
            throw new IllegalArgumentException("Admin accounts cannot be self-registered");

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        log.info("User registered: {} ({})", user.getEmail(), user.getRole());

        String token = jwtService.generateTokenForUser(user);
        return buildAuthResponse(token, user);
    }

    // ── Login ──────────────────────────────────────────────────
    // NOTE: No @Transactional here — prevents rollback-only error
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        // Check DB first (HOST / GUEST)
        var userOpt = userRepository.findByEmail(request.getEmail());

        // Not in DB — must be InMemory ADMIN
        if (userOpt.isEmpty()) {
            String token = jwtUtil.generateToken(request.getEmail(), "ADMIN");
            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .email(request.getEmail())
                    .fullName("Admin User")
                    .role(RoleType.ADMIN)
                    .emailVerified(true)
                    .verificationBadge(true)
                    .build();
        }

        User user = userOpt.get();

        if (!user.isActive())
            throw new IllegalStateException("Account is deactivated");

        String token = jwtService.generateTokenForUser(user);
        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(token, user);
    }

    // ── Helper ─────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .accessToken(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .verificationBadge(user.isVerificationBadge())
                .build();
    }
}
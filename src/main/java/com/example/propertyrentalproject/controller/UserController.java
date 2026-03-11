package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.KycRequest;
import com.example.propertyrentalproject.dto.requests.UpdateProfileRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.UserResponse;
import com.example.propertyrentalproject.service.UserService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Profile, KYC, verification")
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched",
                userService.getProfile(securityUtil.getCurrentUserId())));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                userService.updateProfile(securityUtil.getCurrentUserId(), request)));
    }

    @PostMapping("/kyc")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Submit KYC (HOST only)")
    public ResponseEntity<ApiResponse<String>> submitKyc(
            @Valid @RequestBody KycRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.submitKyc(securityUtil.getCurrentUserId(), request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user public profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched",
                userService.getProfile(id)));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/pending-kyc")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all hosts pending KYC (ADMIN)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getPendingKyc() {
        return ResponseEntity.ok(ApiResponse.success("Pending KYC",
                userService.getPendingKycUsers()));
    }

    @PostMapping("/admin/{id}/approve-kyc")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve host KYC (ADMIN)")
    public ResponseEntity<ApiResponse<String>> approveKyc(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.approveKyc(id, securityUtil.getCurrentUserId())));
    }

    @PostMapping("/admin/{id}/reject-kyc")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject host KYC (ADMIN)")
    public ResponseEntity<ApiResponse<String>> rejectKyc(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.rejectKyc(id, securityUtil.getCurrentUserId(), reason)));
    }

    @PostMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a user (ADMIN)")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.deactivateUser(id)));
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users (ADMIN)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success("Search results",
                userService.searchUsers(query)));
    }
}
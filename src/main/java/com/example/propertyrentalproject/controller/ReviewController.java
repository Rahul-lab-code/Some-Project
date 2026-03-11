package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.ReviewRequest;
import com.example.propertyrentalproject.dto.requests.ReviewResponseRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.ReviewResponse;
import com.example.propertyrentalproject.service.ReviewService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Reviews and ratings management")
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    // ── Submit review (GUEST or HOST) ──────────────────────────

    @PostMapping
    @Operation(summary = "Submit a review (GUEST or HOST)")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted",
                        reviewService.submitReview(
                                securityUtil.getCurrentUserId(), request)));
    }

    // ── Respond to review ──────────────────────────────────────

    @PostMapping("/{id}/respond")
    @Operation(summary = "Respond to a review (reviewee only)")
    public ResponseEntity<ApiResponse<ReviewResponse>> respondToReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewResponseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Response added",
                reviewService.respondToReview(
                        securityUtil.getCurrentUserId(), id, request)));
    }

    // ── Get reviews ────────────────────────────────────────────

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews for a user")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User reviews",
                reviewService.getReviewsForUser(userId)));
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Get reviews written by me")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews() {
        return ResponseEntity.ok(ApiResponse.success("My reviews",
                reviewService.getReviewsByUser(
                        securityUtil.getCurrentUserId())));
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get all reviews for a property")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForProperty(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(ApiResponse.success("Property reviews",
                reviewService.getReviewsForProperty(propertyId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Review fetched",
                reviewService.getReview(id)));
    }

    // ── Ratings ────────────────────────────────────────────────

    @GetMapping("/user/{userId}/average-rating")
    @Operation(summary = "Get average rating for a user")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageRatingForUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Average rating",
                reviewService.getAverageRatingForUser(userId)));
    }

    @GetMapping("/property/{propertyId}/average-rating")
    @Operation(summary = "Get average rating for a property")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageRatingForProperty(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(ApiResponse.success("Average rating",
                reviewService.getAverageRatingForProperty(propertyId)));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all reviews (ADMIN)")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        return ResponseEntity.ok(ApiResponse.success("All reviews",
                reviewService.getAllReviews()));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a review (ADMIN)")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.deleteReview(id)));
    }
}
package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.ReviewRequest;
import com.example.propertyrentalproject.dto.requests.ReviewResponseRequest;
import com.example.propertyrentalproject.dto.responses.ReviewResponse;
import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Booking;
import com.example.propertyrentalproject.model.Review;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.BookingRepository;
import com.example.propertyrentalproject.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    // ── Submit review ──────────────────────────────────────────
    @Transactional
    public ReviewResponse submitReview(Long reviewerId, ReviewRequest req) {
        User reviewer = userService.findById(reviewerId);
        User reviewee = userService.findById(req.getRevieweeId());

        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + req.getBookingId()));

        // Must be COMPLETED
        if (!BookingStatus.COMPLETED.equals(booking.getStatus()))
            throw new IllegalStateException(
                    "Reviews can only be submitted after booking is completed");

        // Reviewer must be guest or host of this booking
        boolean isGuest = booking.getGuest().getId().equals(reviewerId);
        boolean isHost = booking.getHost().getId().equals(reviewerId);

        if (!isGuest && !isHost)
            throw new UnauthorizedException(
                    "You are not part of this booking");

        // Reviewee must be the other party
        boolean revieweeIsHost = booking.getHost().getId()
                .equals(req.getRevieweeId());
        boolean revieweeIsGuest = booking.getGuest().getId()
                .equals(req.getRevieweeId());

        if (!revieweeIsHost && !revieweeIsGuest)
            throw new UnauthorizedException(
                    "Reviewee must be the other party in this booking");

        // Cannot review same person twice for same booking
        if (reviewRepository.existsByBookingIdAndReviewerId(
                req.getBookingId(), reviewerId))
            throw new IllegalStateException(
                    "You have already reviewed this booking");

        Review review = Review.builder()
                .booking(booking)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(req.getRating())
                .cleanliness(req.getCleanliness())
                .communication(req.getCommunication())
                .accuracy(req.getAccuracy())
                .value(req.getValue())
                .ruleCompliance(req.getRuleCompliance())
                .comment(req.getComment())
                .build();

        reviewRepository.save(review);

        // Update reviewee trust score
        userService.updateTrustScore(reviewee.getId(), req.getRating());

        log.info("Review submitted for booking: {} by: {}",
                req.getBookingId(), reviewerId);
        return toResponse(review);
    }

    // ── Respond to review (reviewee responds) ─────────────────
    @Transactional
    public ReviewResponse respondToReview(Long userId, Long reviewId,
                                          ReviewResponseRequest req) {
        Review review = findById(reviewId);

        if (!review.getReviewee().getId().equals(userId))
            throw new UnauthorizedException(
                    "Only the reviewee can respond to this review");

        if (review.getResponse() != null)
            throw new IllegalStateException(
                    "You have already responded to this review");

        review.setResponse(req.getResponse());
        review.setRespondedAt(LocalDateTime.now());
        reviewRepository.save(review);

        log.info("Response added to review: {} by: {}", reviewId, userId);
        return toResponse(review);
    }

    // ── Get reviews for a user ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForUser(Long userId) {
        return reviewRepository.findByRevieweeId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get reviews written by a user ──────────────────────────
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByReviewerId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get reviews for a property ─────────────────────────────
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForProperty(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get average rating for user ────────────────────────────
    @Transactional(readOnly = true)
    public BigDecimal getAverageRatingForUser(Long userId) {
        return reviewRepository.findAverageRatingByUserId(userId)
                .orElse(BigDecimal.ZERO);
    }

    // ── Get average rating for property ───────────────────────
    @Transactional(readOnly = true)
    public BigDecimal getAverageRatingForProperty(Long propertyId) {
        return reviewRepository.findAverageRatingByPropertyId(propertyId)
                .orElse(BigDecimal.ZERO);
    }

    // ── Get review by ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        return toResponse(findById(reviewId));
    }

    // ── Admin: Get all reviews ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: Delete review ───────────────────────────────────
    @Transactional
    public String deleteReview(Long reviewId) {
        Review review = findById(reviewId);
        reviewRepository.delete(review);
        log.info("Review deleted: {}", reviewId);
        return "Review deleted successfully";
    }

    // ── Helpers ────────────────────────────────────────────────
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found: " + id));
    }

    public ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .bookingId(r.getBooking().getId())
                .reviewerId(r.getReviewer().getId())
                .reviewerName(r.getReviewer().getFullName())
                .revieweeId(r.getReviewee().getId())
                .revieweeName(r.getReviewee().getFullName())
                .rating(r.getRating())
                .cleanliness(r.getCleanliness())
                .communication(r.getCommunication())
                .accuracy(r.getAccuracy())
                .value(r.getValue())
                .ruleCompliance(r.getRuleCompliance())
                .comment(r.getComment())
                .response(r.getResponse())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
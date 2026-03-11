package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRevieweeId(Long revieweeId);

    List<Review> findByReviewerId(Long reviewerId);

    List<Review> findByBookingId(Long bookingId);

    // Check if reviewer already reviewed this booking
    boolean existsByBookingIdAndReviewerId(Long bookingId, Long reviewerId);

    // Get reviews for a property (via booking)
    @Query("""
        SELECT r FROM Review r
        WHERE r.booking.property.id = :propertyId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByPropertyId(@Param("propertyId") Long propertyId);

    // Average rating for a user
    @Query("""
        SELECT AVG(r.rating) FROM Review r
        WHERE r.reviewee.id = :userId
    """)
    Optional<BigDecimal> findAverageRatingByUserId(
            @Param("userId") Long userId);

    // Average rating for a property
    @Query("""
        SELECT AVG(r.rating) FROM Review r
        WHERE r.booking.property.id = :propertyId
    """)
    Optional<BigDecimal> findAverageRatingByPropertyId(
            @Param("propertyId") Long propertyId);
}
package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestId(Long guestId);

    List<Booking> findByHostId(Long hostId);

    List<Booking> findByPropertyId(Long propertyId);

    List<Booking> findByGuestIdAndStatus(Long guestId, BookingStatus status);

    List<Booking> findByHostIdAndStatus(Long hostId, BookingStatus status);

    // Check if property is available for given dates
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b WHERE
        b.property.id = :propertyId
        AND b.status NOT IN ('CANCELLED', 'REJECTED')
        AND (
            (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)
        )
    """)
    boolean existsOverlappingBooking(
            @Param("propertyId") Long propertyId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Get bookings by property and status
    List<Booking> findByPropertyIdAndStatus(Long propertyId, BookingStatus status);

    // Get active booking for check-in code verification
    @Query("""
        SELECT b FROM Booking b WHERE
        b.checkInCode = :code
        AND b.status = 'CONFIRMED'
    """)
    Booking findByCheckInCode(@Param("code") String code);
}
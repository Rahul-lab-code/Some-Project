package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.enums.PropertyStatus;
import com.example.propertyrentalproject.enums.PropertyType;
import com.example.propertyrentalproject.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByHostId(Long hostId);

    List<Property> findByStatus(PropertyStatus status);

    List<Property> findByHostIdAndStatus(Long hostId, PropertyStatus status);

    @Query("""
        SELECT p FROM Property p WHERE
        p.status = 'PUBLISHED'
        AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:propertyType IS NULL OR p.propertyType = :propertyType)
        AND (:minPrice IS NULL OR p.basePrice >= :minPrice)
        AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)
        AND (:guests IS NULL OR p.capacity >= :guests)
    """)
    List<Property> searchProperties(
            @Param("city") String city,
            @Param("propertyType") PropertyType propertyType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("guests") Integer guests
    );

    @Query("""
        SELECT p FROM Property p WHERE
        p.status = 'PUBLISHED'
        AND p.id NOT IN (
            SELECT ca.property.id FROM CalendarAvailability ca
            WHERE ca.blockedDate BETWEEN :checkIn AND :checkOut
        )
        AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:guests IS NULL OR p.capacity >= :guests)
    """)
    List<Property> searchAvailableProperties(
            @Param("city") String city,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guests") Integer guests
    );
}

package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.model.CalendarAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarAvailabilityRepository
        extends JpaRepository<CalendarAvailability, Long> {

    List<CalendarAvailability> findByPropertyId(Long propertyId);

    List<CalendarAvailability> findByPropertyIdAndBlockedDateBetween(
            Long propertyId, LocalDate start, LocalDate end);

    boolean existsByPropertyIdAndBlockedDate(Long propertyId, LocalDate date);

    void deleteByPropertyIdAndBlockedDate(Long propertyId, LocalDate date);
}
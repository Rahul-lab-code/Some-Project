package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.BlockCalendarRequest;
import com.example.propertyrentalproject.dto.responses.CalendarResponse;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.CalendarAvailability;
import com.example.propertyrentalproject.model.Property;
import com.example.propertyrentalproject.repository.CalendarAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarAvailabilityRepository calendarRepository;
    private final PropertyService propertyService;

    // ── Block dates ────────────────────────────────────────────
    @Transactional
    public List<CalendarResponse> blockDates(Long hostId, Long propertyId,
                                             BlockCalendarRequest req) {
        Property property = propertyService.findById(propertyId);

        if (!property.getHost().getId().equals(hostId))
            throw new UnauthorizedException("You are not the owner of this property");

        List<CalendarAvailability> blocked = req.getDates().stream()
                .filter(date -> !calendarRepository
                        .existsByPropertyIdAndBlockedDate(propertyId, date))
                .map(date -> CalendarAvailability.builder()
                        .property(property)
                        .blockedDate(date)
                        .reason(req.getReason())
                        .build())
                .collect(Collectors.toList());

        calendarRepository.saveAll(blocked);
        log.info("Blocked {} dates for property: {}", blocked.size(), propertyId);
        return blocked.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Unblock dates ──────────────────────────────────────────
    @Transactional
    public String unblockDate(Long hostId, Long propertyId, LocalDate date) {
        Property property = propertyService.findById(propertyId);

        if (!property.getHost().getId().equals(hostId))
            throw new UnauthorizedException("You are not the owner of this property");

        calendarRepository.deleteByPropertyIdAndBlockedDate(propertyId, date);
        log.info("Unblocked date {} for property: {}", date, propertyId);
        return "Date unblocked successfully";
    }

    // ── Get blocked dates ──────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CalendarResponse> getBlockedDates(Long propertyId) {
        return calendarRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get blocked dates in range ─────────────────────────────
    @Transactional(readOnly = true)
    public List<CalendarResponse> getBlockedDatesInRange(Long propertyId,
                                                         LocalDate start,
                                                         LocalDate end) {
        return calendarRepository
                .findByPropertyIdAndBlockedDateBetween(propertyId, start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Helper ─────────────────────────────────────────────────
    private CalendarResponse toResponse(CalendarAvailability c) {
        return CalendarResponse.builder()
                .id(c.getId())
                .propertyId(c.getProperty().getId())
                .blockedDate(c.getBlockedDate())
                .reason(c.getReason())
                .bookingId(c.getBookingId())
                .build();
    }
}

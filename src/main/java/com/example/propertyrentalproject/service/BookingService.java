package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.CreateBookingRequest;
import com.example.propertyrentalproject.dto.requests.UpdateBookingStatusRequest;
import com.example.propertyrentalproject.dto.responses.BookingResponse;
import com.example.propertyrentalproject.enums.BookingMode;
import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.enums.PropertyStatus;
import com.example.propertyrentalproject.exception.BookingConflictException;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Booking;
import com.example.propertyrentalproject.model.CalendarAvailability;
import com.example.propertyrentalproject.model.Property;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.BookingRepository;
import com.example.propertyrentalproject.repository.CalendarAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CalendarAvailabilityRepository calendarRepository;
    private final PropertyService propertyService;
    private final UserService userService;

    // ── Create booking ─────────────────────────────────────────
    @Transactional
    public BookingResponse createBooking(Long guestId,
                                         CreateBookingRequest req) {
        User guest = userService.findById(guestId);
        Property property = propertyService.findById(req.getPropertyId());

        // Validations
        if (!PropertyStatus.PUBLISHED.equals(property.getStatus()))
            throw new IllegalStateException("Property is not available for booking");

        if (property.getHost().getId().equals(guestId))
            throw new IllegalArgumentException("You cannot book your own property");

        if (req.getCheckInDate().isEqual(req.getCheckOutDate()) ||
                req.getCheckInDate().isAfter(req.getCheckOutDate()))
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date");

        long nights = ChronoUnit.DAYS.between(
                req.getCheckInDate(), req.getCheckOutDate());

        if (nights < property.getMinStayNights())
            throw new IllegalArgumentException(
                    "Minimum stay is " + property.getMinStayNights() + " nights");

        if (nights > property.getMaxStayNights())
            throw new IllegalArgumentException(
                    "Maximum stay is " + property.getMaxStayNights() + " nights");

        if (req.getGuestsCount() > property.getCapacity())
            throw new IllegalArgumentException(
                    "Property capacity is " + property.getCapacity() + " guests");

        // Check for overlapping bookings
        if (bookingRepository.existsOverlappingBooking(
                req.getPropertyId(), req.getCheckInDate(), req.getCheckOutDate()))
            throw new BookingConflictException(
                    "Property is already booked for selected dates");

        // Check calendar blocks
        List<LocalDate> blockedDates = calendarRepository
                .findByPropertyIdAndBlockedDateBetween(
                        req.getPropertyId(),
                        req.getCheckInDate(),
                        req.getCheckOutDate().minusDays(1))
                .stream()
                .map(CalendarAvailability::getBlockedDate)
                .collect(Collectors.toList());

        if (!blockedDates.isEmpty())
            throw new BookingConflictException(
                    "Some selected dates are blocked by the host");

        // Calculate pricing
        BigDecimal baseAmount = property.getBasePrice()
                .multiply(BigDecimal.valueOf(nights));
        BigDecimal cleaningFee = property.getCleaningFee() != null ?
                property.getCleaningFee() : BigDecimal.ZERO;
        BigDecimal serviceFee = baseAmount
                .multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = baseAmount.add(serviceFee)
                .multiply(BigDecimal.valueOf(0.18))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal securityDeposit = property.getSecurityDeposit() != null ?
                property.getSecurityDeposit() : BigDecimal.ZERO;
        BigDecimal totalAmount = baseAmount
                .add(cleaningFee)
                .add(serviceFee)
                .add(taxAmount)
                .add(securityDeposit);

        // Determine initial status
        BookingStatus initialStatus = BookingMode.INSTANT.equals(
                property.getBookingMode()) ?
                BookingStatus.CONFIRMED : BookingStatus.PENDING;

        // Generate check-in code for confirmed bookings
        String checkInCode = BookingStatus.CONFIRMED.equals(initialStatus) ?
                UUID.randomUUID().toString().substring(0, 8).toUpperCase() : null;

        Booking booking = Booking.builder()
                .guest(guest)
                .property(property)
                .host(property.getHost())
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .guestsCount(req.getGuestsCount())
                .status(initialStatus)
                .cancellationPolicy(property.getCancellationPolicy())
                .baseAmount(baseAmount)
                .cleaningFee(cleaningFee)
                .serviceFee(serviceFee)
                .taxAmount(taxAmount)
                .securityDeposit(securityDeposit)
                .totalAmount(totalAmount)
                .checkInCode(checkInCode)
                .build();

        bookingRepository.save(booking);

        // Block calendar dates for confirmed bookings
        if (BookingStatus.CONFIRMED.equals(initialStatus)) {
            blockDatesForBooking(property, booking,
                    req.getCheckInDate(), req.getCheckOutDate());
        }

        log.info("Booking created: {} status: {}", booking.getId(), initialStatus);
        return toResponse(booking);
    }

    // ── Host: Confirm or Reject booking (APPROVAL_BASED) ──────
    @Transactional
    public BookingResponse updateBookingStatus(Long hostId, Long bookingId,
                                               UpdateBookingStatusRequest req) {
        Booking booking = findById(bookingId);

        if (!booking.getHost().getId().equals(hostId))
            throw new UnauthorizedException(
                    "You are not the host of this booking");

        BookingStatus newStatus = req.getStatus();

        if (BookingStatus.CONFIRMED.equals(newStatus)) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setCheckInCode(
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            blockDatesForBooking(
                    booking.getProperty(), booking,
                    booking.getCheckInDate(), booking.getCheckOutDate());

        } else if (BookingStatus.REJECTED.equals(newStatus)) {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setCancellationReason(req.getReason());
            booking.setCancelledAt(LocalDateTime.now());

        } else if (BookingStatus.CHECKED_IN.equals(newStatus)) {
            if (!BookingStatus.CONFIRMED.equals(booking.getStatus()))
                throw new IllegalStateException(
                        "Booking must be CONFIRMED before check-in");
            booking.setStatus(BookingStatus.CHECKED_IN);
            booking.setActualCheckIn(LocalDateTime.now());

        } else if (BookingStatus.CHECKED_OUT.equals(newStatus)) {
            if (!BookingStatus.CHECKED_IN.equals(booking.getStatus()))
                throw new IllegalStateException(
                        "Booking must be CHECKED_IN before check-out");
            booking.setStatus(BookingStatus.CHECKED_OUT);
            booking.setActualCheckOut(LocalDateTime.now());

        } else if (BookingStatus.COMPLETED.equals(newStatus)) {
            if (!BookingStatus.CHECKED_OUT.equals(booking.getStatus()))
                throw new IllegalStateException(
                        "Booking must be CHECKED_OUT before completing");
            booking.setStatus(BookingStatus.COMPLETED);
        }

        bookingRepository.save(booking);
        log.info("Booking {} status updated to {}", bookingId, newStatus);
        return toResponse(booking);
    }

    // ── Guest: Cancel booking ──────────────────────────────────
    @Transactional
    public BookingResponse cancelBooking(Long guestId, Long bookingId,
                                         String reason) {
        Booking booking = findById(bookingId);

        if (!booking.getGuest().getId().equals(guestId))
            throw new UnauthorizedException(
                    "You are not the guest of this booking");

        if (BookingStatus.CHECKED_IN.equals(booking.getStatus()) ||
                BookingStatus.COMPLETED.equals(booking.getStatus()))
            throw new IllegalStateException(
                    "Cannot cancel a booking that is checked-in or completed");

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelledBy("GUEST");

        bookingRepository.save(booking);
        log.info("Booking {} cancelled by guest {}", bookingId, guestId);
        return toResponse(booking);
    }

    // ── Get booking by ID ──────────────────────────────────────
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = findById(bookingId);

        if (!booking.getGuest().getId().equals(userId) &&
                !booking.getHost().getId().equals(userId))
            throw new UnauthorizedException(
                    "You are not authorized to view this booking");

        return toResponse(booking);
    }

    // ── Guest: Get my bookings ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookingsAsGuest(Long guestId) {
        return bookingRepository.findByGuestId(guestId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Host: Get bookings for my properties ───────────────────
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookingsAsHost(Long hostId) {
        return bookingRepository.findByHostId(hostId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: Get all bookings ────────────────────────────────
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + id));
    }

    private void blockDatesForBooking(Property property, Booking booking,
                                      LocalDate checkIn, LocalDate checkOut) {
        checkIn.datesUntil(checkOut).forEach(date -> {
            if (!calendarRepository.existsByPropertyIdAndBlockedDate(
                    property.getId(), date)) {
                calendarRepository.save(
                        CalendarAvailability.builder()
                                .property(property)
                                .blockedDate(date)
                                .reason("BOOKED")
                                .bookingId(booking.getId())
                                .build()
                );
            }
        });
    }

    public BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .guestId(b.getGuest().getId())
                .guestName(b.getGuest().getFullName())
                .propertyId(b.getProperty().getId())
                .propertyTitle(b.getProperty().getTitle())
                .hostId(b.getHost().getId())
                .hostName(b.getHost().getFullName())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .guestsCount(b.getGuestsCount())
                .status(b.getStatus())
                .cancellationPolicy(b.getCancellationPolicy())
                .baseAmount(b.getBaseAmount())
                .cleaningFee(b.getCleaningFee())
                .serviceFee(b.getServiceFee())
                .taxAmount(b.getTaxAmount())
                .securityDeposit(b.getSecurityDeposit())
                .totalAmount(b.getTotalAmount())
                .checkInCode(b.getCheckInCode())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
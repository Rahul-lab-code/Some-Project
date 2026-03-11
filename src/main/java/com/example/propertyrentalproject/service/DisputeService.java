package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.DisputeRequest;
import com.example.propertyrentalproject.dto.requests.ResolveDisputeRequest;
import com.example.propertyrentalproject.dto.responses.DisputeResponse;
import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Booking;
import com.example.propertyrentalproject.model.Dispute;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.BookingRepository;
import com.example.propertyrentalproject.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    // ── Raise a dispute ────────────────────────────────────────
    @Transactional
    public DisputeResponse raiseDispute(Long userId, DisputeRequest req) {
        User raisedBy = userService.findById(userId);

        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + req.getBookingId()));

        // Must be guest or host of this booking
        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isHost  = booking.getHost().getId().equals(userId);

        if (!isGuest && !isHost)
            throw new UnauthorizedException(
                    "You are not part of this booking");

        // Booking must be active or completed
        if (BookingStatus.PENDING.equals(booking.getStatus()) ||
                BookingStatus.REJECTED.equals(booking.getStatus()))
            throw new IllegalStateException(
                    "Cannot raise a dispute for a PENDING or REJECTED booking");

        // One dispute per user per booking
        if (disputeRepository.existsByBookingIdAndRaisedById(
                req.getBookingId(), userId))
            throw new IllegalStateException(
                    "You have already raised a dispute for this booking");

        Dispute dispute = Dispute.builder()
                .booking(booking)
                .raisedBy(raisedBy)
                .type(req.getType())
                .status(DisputeStatus.OPEN)
                .description(req.getDescription())
                .evidenceUrls(req.getEvidenceUrls())
                .build();

        disputeRepository.save(dispute);
        log.info("Dispute raised for booking: {} by user: {}",
                req.getBookingId(), userId);
        return toResponse(dispute);
    }

    // ── Admin: Update dispute status ───────────────────────────
    @Transactional
    public DisputeResponse updateDisputeStatus(Long adminId,
                                               Long disputeId,
                                               DisputeStatus status) {
        Dispute dispute = findById(disputeId);

        dispute.setStatus(status);
        disputeRepository.save(dispute);

        log.info("Dispute {} status updated to {} by admin {}",
                disputeId, status, adminId);
        return toResponse(dispute);
    }

    // ── Admin: Resolve dispute ─────────────────────────────────
    @Transactional
    public DisputeResponse resolveDispute(Long adminId, Long disputeId,
                                          ResolveDisputeRequest req) {
        Dispute dispute = findById(disputeId);

        if (DisputeStatus.RESOLVED.equals(dispute.getStatus()) ||
                DisputeStatus.CLOSED.equals(dispute.getStatus()))
            throw new IllegalStateException(
                    "Dispute is already resolved or closed");

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolution(req.getResolution());
        dispute.setResolvedBy(adminId);
        dispute.setResolvedAt(LocalDateTime.now());

        disputeRepository.save(dispute);
        log.info("Dispute {} resolved by admin {}", disputeId, adminId);
        return toResponse(dispute);
    }

    // ── Admin: Close dispute ───────────────────────────────────
    @Transactional
    public DisputeResponse closeDispute(Long adminId, Long disputeId) {
        Dispute dispute = findById(disputeId);

        dispute.setStatus(DisputeStatus.CLOSED);
        dispute.setResolvedBy(adminId);
        dispute.setResolvedAt(LocalDateTime.now());

        disputeRepository.save(dispute);
        log.info("Dispute {} closed by admin {}", disputeId, adminId);
        return toResponse(dispute);
    }

    // ── Get dispute by ID ──────────────────────────────────────
    @Transactional(readOnly = true)
    public DisputeResponse getDispute(Long disputeId, Long userId) {
        Dispute dispute = findById(disputeId);

        boolean isGuest = dispute.getBooking().getGuest().getId().equals(userId);
        boolean isHost  = dispute.getBooking().getHost().getId().equals(userId);
        boolean isRaiser = dispute.getRaisedBy().getId().equals(userId);

        if (!isGuest && !isHost && !isRaiser)
            throw new UnauthorizedException(
                    "You are not authorized to view this dispute");

        return toResponse(dispute);
    }

    // ── Get my disputes ────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DisputeResponse> getMyDisputes(Long userId) {
        return disputeRepository.findByRaisedById(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get disputes for a booking ─────────────────────────────
    @Transactional(readOnly = true)
    public List<DisputeResponse> getDisputesByBooking(Long bookingId,
                                                      Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + bookingId));

        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isHost  = booking.getHost().getId().equals(userId);

        if (!isGuest && !isHost)
            throw new UnauthorizedException(
                    "You are not part of this booking");

        return disputeRepository.findByBookingId(bookingId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: Get all disputes ────────────────────────────────
    @Transactional(readOnly = true)
    public List<DisputeResponse> getAllDisputes() {
        return disputeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: Get disputes by status ──────────────────────────
    @Transactional(readOnly = true)
    public List<DisputeResponse> getDisputesByStatus(DisputeStatus status) {
        return disputeRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────
    public Dispute findById(Long id) {
        return disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dispute not found: " + id));
    }

    public DisputeResponse toResponse(Dispute d) {
        return DisputeResponse.builder()
                .id(d.getId())
                .bookingId(d.getBooking().getId())
                .raisedById(d.getRaisedBy().getId())
                .raisedByName(d.getRaisedBy().getFullName())
                .type(d.getType())
                .status(d.getStatus())
                .description(d.getDescription())
                .resolution(d.getResolution())
                .evidenceUrls(d.getEvidenceUrls())
                .createdAt(d.getCreatedAt())
                .resolvedAt(d.getResolvedAt())
                .build();
    }
}
package com.example.propertyrentalproject.service;
 
import com.example.propertyrentalproject.dto.requests.MaintenanceRequest;
import com.example.propertyrentalproject.dto.responses.MaintenanceResponse;
import com.example.propertyrentalproject.enums.MaintenanceStatus;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Booking;
import com.example.propertyrentalproject.model.MaintenanceTicket;
import com.example.propertyrentalproject.model.Property;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.BookingRepository;
import com.example.propertyrentalproject.repository.MaintenanceTicketRepository;
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
public class MaintenanceService {
 
    private final MaintenanceTicketRepository maintenanceRepository;
    private final PropertyService propertyService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
 
    // ── Create ticket ──────────────────────────────────────────
    @Transactional
    public MaintenanceResponse createTicket(Long userId,
                                             MaintenanceRequest req) {
        User reportedBy = userService.findById(userId);
        Property property = propertyService.findById(req.getPropertyId());
 
        // Only host or guest of an active booking can report
        boolean isHost = property.getHost().getId().equals(userId);
        boolean isGuest = false;
 
        Booking booking = null;
        if (req.getBookingId() != null) {
            booking = bookingRepository.findById(req.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Booking not found: " + req.getBookingId()));
            isGuest = booking.getGuest().getId().equals(userId);
        }
 
        if (!isHost && !isGuest)
            throw new UnauthorizedException(
                    "Only the host or active guest can report maintenance");
 
        MaintenanceTicket ticket = MaintenanceTicket.builder()
                .property(property)
                .booking(booking)
                .reportedBy(reportedBy)
                .title(req.getTitle())
                .description(req.getDescription())
                .status(MaintenanceStatus.OPEN)
                .photoUrls(req.getPhotoUrls())
                .build();
 
        maintenanceRepository.save(ticket);
        log.info("Maintenance ticket created for property: {} by: {}",
                req.getPropertyId(), userId);
        return toResponse(ticket);
    }
 
    // ── Host: Update ticket status ─────────────────────────────
    @Transactional
    public MaintenanceResponse updateTicketStatus(Long hostId,
                                                   Long ticketId,
                                                   MaintenanceStatus status,
                                                   String resolutionNote) {
        MaintenanceTicket ticket = findById(ticketId);
 
        if (!ticket.getProperty().getHost().getId().equals(hostId))
            throw new UnauthorizedException(
                    "Only the property host can update this ticket");
 
        ticket.setStatus(status);
 
        if (resolutionNote != null)
            ticket.setResolutionNote(resolutionNote);
 
        if (MaintenanceStatus.RESOLVED.equals(status) ||
                MaintenanceStatus.CLOSED.equals(status))
            ticket.setResolvedAt(LocalDateTime.now());
 
        maintenanceRepository.save(ticket);
        log.info("Maintenance ticket {} updated to {}", ticketId, status);
        return toResponse(ticket);
    }
 
    // ── Get ticket by ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public MaintenanceResponse getTicket(Long ticketId, Long userId) {
        MaintenanceTicket ticket = findById(ticketId);
 
        boolean isHost = ticket.getProperty().getHost().getId().equals(userId);
        boolean isReporter = ticket.getReportedBy().getId().equals(userId);
 
        if (!isHost && !isReporter)
            throw new UnauthorizedException(
                    "You are not authorized to view this ticket");
 
        return toResponse(ticket);
    }
 
    // ── Get tickets for a property ─────────────────────────────
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getTicketsByProperty(Long propertyId,
                                                           Long hostId) {
        Property property = propertyService.findById(propertyId);
 
        if (!property.getHost().getId().equals(hostId))
            throw new UnauthorizedException(
                    "Only the property host can view these tickets");
 
        return maintenanceRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
 
    // ── Get my reported tickets ────────────────────────────────
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getMyTickets(Long userId) {
        return maintenanceRepository.findByReportedById(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
 
    // ── Admin: Get all tickets ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getAllTickets() {
        return maintenanceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
 
    // ── Admin: Get tickets by status ───────────────────────────
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getTicketsByStatus(
            MaintenanceStatus status) {
        return maintenanceRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
 
    // ── Helpers ────────────────────────────────────────────────
    public MaintenanceTicket findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance ticket not found: " + id));
    }
 
    public MaintenanceResponse toResponse(MaintenanceTicket t) {
        return MaintenanceResponse.builder()
                .id(t.getId())
                .propertyId(t.getProperty().getId())
                .propertyTitle(t.getProperty().getTitle())
                .bookingId(t.getBooking() != null ?
                        t.getBooking().getId() : null)
                .reportedById(t.getReportedBy().getId())
                .reportedByName(t.getReportedBy().getFullName())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .resolutionNote(t.getResolutionNote())
                .photoUrls(t.getPhotoUrls())
                .createdAt(t.getCreatedAt())
                .resolvedAt(t.getResolvedAt())
                .build();
    }
}
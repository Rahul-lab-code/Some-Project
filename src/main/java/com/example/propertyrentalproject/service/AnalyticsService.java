package com.example.propertyrentalproject.service;
 
import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.enums.KycStatus;
import com.example.propertyrentalproject.enums.MaintenanceStatus;
import com.example.propertyrentalproject.enums.PaymentStatus;
import com.example.propertyrentalproject.enums.PropertyStatus;
import com.example.propertyrentalproject.enums.RoleType;
import com.example.propertyrentalproject.repository.BookingRepository;
import com.example.propertyrentalproject.repository.DisputeRepository;
import com.example.propertyrentalproject.repository.MaintenanceTicketRepository;
import com.example.propertyrentalproject.repository.PaymentRepository;
import com.example.propertyrentalproject.repository.PropertyRepository;
import com.example.propertyrentalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
 
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final DisputeRepository disputeRepository;
    private final MaintenanceTicketRepository maintenanceRepository;
 
    // ── User stats ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalUsers", userRepository.count());
        stats.put("totalHosts",
                userRepository.findByRole(RoleType.HOST).size());
        stats.put("totalGuests",
                userRepository.findByRole(RoleType.GUEST).size());
        stats.put("pendingKyc",
                userRepository.findByRoleAndKycStatus(
                        RoleType.HOST, KycStatus.PENDING_REVIEW).size());
        stats.put("approvedKyc",
                userRepository.findByRoleAndKycStatus(
                        RoleType.HOST, KycStatus.APPROVED).size());
 
        return stats;
    }
 
    // ── Property stats ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getPropertyStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalProperties",
                propertyRepository.count());
        stats.put("publishedProperties",
                propertyRepository.findByStatus(
                        PropertyStatus.PUBLISHED).size());
        stats.put("draftProperties",
                propertyRepository.findByStatus(
                        PropertyStatus.DRAFT).size());
        stats.put("unpublishedProperties",
                propertyRepository.findByStatus(
                        PropertyStatus.UNPUBLISHED).size());
        stats.put("archivedProperties",
                propertyRepository.findByStatus(
                        PropertyStatus.ARCHIVED).size());
 
        return stats;
    }
 
    // ── Booking stats ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getBookingStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalBookings",
                bookingRepository.count());
        stats.put("pendingBookings",
                bookingRepository.findAll().stream()
                        .filter(b -> BookingStatus.PENDING
                                .equals(b.getStatus())).count());
        stats.put("confirmedBookings",
                bookingRepository.findAll().stream()
                        .filter(b -> BookingStatus.CONFIRMED
                                .equals(b.getStatus())).count());
        stats.put("completedBookings",
                bookingRepository.findAll().stream()
                        .filter(b -> BookingStatus.COMPLETED
                                .equals(b.getStatus())).count());
        stats.put("cancelledBookings",
                bookingRepository.findAll().stream()
                        .filter(b -> BookingStatus.CANCELLED
                                .equals(b.getStatus())).count());
 
        return stats;
    }
 
    // ── Payment / Revenue stats ────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalPayments",
                paymentRepository.count());
 
        BigDecimal totalRevenue = paymentRepository.findAll()
                .stream()
                .filter(p -> PaymentStatus.COMPLETED.equals(p.getStatus())
&& "BOOKING_PAYMENT".equals(p.getTransactionType()))
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
 
        BigDecimal totalEscrow = paymentRepository.findAll()
                .stream()
                .filter(p -> PaymentStatus.COMPLETED.equals(p.getStatus())
&& "SECURITY_DEPOSIT".equals(p.getTransactionType()))
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
 
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalEscrowHeld", totalEscrow);
        stats.put("completedPayments",
                paymentRepository.findByStatus(PaymentStatus.COMPLETED).size());
        stats.put("refundedPayments",
                paymentRepository.findByStatus(PaymentStatus.REFUNDED).size());
 
        return stats;
    }
 
    // ── Dispute stats ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getDisputeStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalDisputes",
                disputeRepository.count());
        stats.put("openDisputes",
                disputeRepository.findByStatus(DisputeStatus.OPEN).size());
        stats.put("underReviewDisputes",
                disputeRepository.findByStatus(
                        DisputeStatus.UNDER_REVIEW).size());
        stats.put("resolvedDisputes",
                disputeRepository.findByStatus(
                        DisputeStatus.RESOLVED).size());
        stats.put("closedDisputes",
                disputeRepository.findByStatus(DisputeStatus.CLOSED).size());
 
        return stats;
    }
 
    // ── Maintenance stats ──────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getMaintenanceStats() {
        Map<String, Object> stats = new HashMap<>();
 
        stats.put("totalTickets",
                maintenanceRepository.count());
        stats.put("openTickets",
                maintenanceRepository.findByStatus(
                        MaintenanceStatus.OPEN).size());
        stats.put("inProgressTickets",
                maintenanceRepository.findByStatus(
                        MaintenanceStatus.IN_PROGRESS).size());
        stats.put("resolvedTickets",
                maintenanceRepository.findByStatus(
                        MaintenanceStatus.RESOLVED).size());
 
        return stats;
    }
 
    // ── Full dashboard ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
 
        dashboard.put("users",       getUserStats());
        dashboard.put("properties",  getPropertyStats());
        dashboard.put("bookings",    getBookingStats());
        dashboard.put("revenue",     getRevenueStats());
        dashboard.put("disputes",    getDisputeStats());
        dashboard.put("maintenance", getMaintenanceStats());
 
        return dashboard;
    }
}
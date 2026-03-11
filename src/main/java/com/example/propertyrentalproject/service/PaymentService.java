package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.PaymentRequest;
import com.example.propertyrentalproject.dto.responses.EscrowResponse;
import com.example.propertyrentalproject.dto.responses.PaymentResponse;
import com.example.propertyrentalproject.enums.BookingStatus;
import com.example.propertyrentalproject.enums.PaymentStatus;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Booking;
import com.example.propertyrentalproject.model.EscrowDeposit;
import com.example.propertyrentalproject.model.Payment;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.EscrowDepositRepository;
import com.example.propertyrentalproject.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EscrowDepositRepository escrowRepository;
    private final BookingService bookingService;
    private final UserService userService;

    // ── Pay for booking ────────────────────────────────────────
    @Transactional
    public PaymentResponse payForBooking(Long guestId, PaymentRequest req) {
        Booking booking = bookingService.findById(req.getBookingId());
        User guest = userService.findById(guestId);

        // Validations
        if (!booking.getGuest().getId().equals(guestId))
            throw new UnauthorizedException(
                    "You are not the guest of this booking");

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus()))
            throw new IllegalStateException(
                    "Booking must be CONFIRMED before payment");

        if (paymentRepository.existsByBookingIdAndTransactionType(
                req.getBookingId(), "BOOKING_PAYMENT"))
            throw new IllegalStateException(
                    "Payment already made for this booking");

        // Simulate payment processing
        String transactionId = "TXN_" +
                UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        // Main booking payment (total - security deposit)
        BigDecimal paymentAmount = booking.getTotalAmount()
                .subtract(booking.getSecurityDeposit() != null ?
                        booking.getSecurityDeposit() : BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .booking(booking)
                .payer(guest)
                .amount(paymentAmount)
                .paymentMethod(req.getPaymentMethod())
                .status(PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .transactionType("BOOKING_PAYMENT")
                .build();

        paymentRepository.save(payment);

        // Create escrow for security deposit if applicable
        if (booking.getSecurityDeposit() != null &&
                booking.getSecurityDeposit().compareTo(BigDecimal.ZERO) > 0) {

            String escrowTxnId = "TXN_" +
                    UUID.randomUUID().toString().substring(0, 12).toUpperCase();

            Payment escrowPayment = Payment.builder()
                    .booking(booking)
                    .payer(guest)
                    .amount(booking.getSecurityDeposit())
                    .paymentMethod(req.getPaymentMethod())
                    .status(PaymentStatus.COMPLETED)
                    .transactionId(escrowTxnId)
                    .transactionType("SECURITY_DEPOSIT")
                    .build();

            paymentRepository.save(escrowPayment);

            // Hold in escrow
            EscrowDeposit escrow = EscrowDeposit.builder()
                    .booking(booking)
                    .amount(booking.getSecurityDeposit())
                    .status("HELD")
                    .build();

            escrowRepository.save(escrow);
            log.info("Security deposit held in escrow for booking: {}",
                    booking.getId());
        }

        log.info("Payment completed for booking: {} txn: {}",
                booking.getId(), transactionId);
        return toPaymentResponse(payment);
    }

    // ── Release escrow to host ─────────────────────────────────
    @Transactional
    public EscrowResponse releaseEscrow(Long adminId, Long bookingId) {
        EscrowDeposit escrow = escrowRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Escrow not found for booking: " + bookingId));

        if (!"HELD".equals(escrow.getStatus()))
            throw new IllegalStateException(
                    "Escrow is not in HELD status, current: " + escrow.getStatus());

        escrow.setStatus("RELEASED");
        escrow.setReleasedAt(LocalDateTime.now());
        escrowRepository.save(escrow);

        log.info("Escrow released for booking: {} by admin: {}",
                bookingId, adminId);
        return toEscrowResponse(escrow);
    }

    // ── Deduct from escrow (damage claim) ─────────────────────
    @Transactional
    public EscrowResponse deductFromEscrow(Long adminId, Long bookingId,
                                           BigDecimal amount, String reason) {
        EscrowDeposit escrow = escrowRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Escrow not found for booking: " + bookingId));

        if (!"HELD".equals(escrow.getStatus()))
            throw new IllegalStateException(
                    "Escrow is not in HELD status");

        if (amount.compareTo(escrow.getAmount()) > 0)
            throw new IllegalArgumentException(
                    "Deduction amount exceeds escrow balance");

        escrow.setDeductedAmount(amount);
        escrow.setDeductionReason(reason);

        // If full deduction, forfeit. Otherwise partial deduction.
        if (amount.compareTo(escrow.getAmount()) == 0) {
            escrow.setStatus("FORFEITED");
        } else {
            escrow.setStatus("PARTIALLY_DEDUCTED");
        }

        escrowRepository.save(escrow);
        log.info("Escrow deducted ₹{} for booking: {} reason: {}",
                amount, bookingId, reason);
        return toEscrowResponse(escrow);
    }

    // ── Refund booking ─────────────────────────────────────────
    @Transactional
    public PaymentResponse refundBooking(Long adminId, Long bookingId) {
        Payment payment = paymentRepository
                .findByBookingIdAndTransactionType(bookingId, "BOOKING_PAYMENT")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for booking: " + bookingId));

        if (!PaymentStatus.COMPLETED.equals(payment.getStatus()))
            throw new IllegalStateException(
                    "Payment is not completed, cannot refund");

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Refund processed for booking: {} by admin: {}",
                bookingId, adminId);
        return toPaymentResponse(payment);
    }

    // ── Get payments for booking ───────────────────────────────
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId,
                                                      Long userId) {
        Booking booking = bookingService.findById(bookingId);

        if (!booking.getGuest().getId().equals(userId) &&
                !booking.getHost().getId().equals(userId))
            throw new UnauthorizedException(
                    "You are not authorized to view these payments");

        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    // ── Get my payment history (guest) ─────────────────────────
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(Long guestId) {
        return paymentRepository.findByPayerId(guestId)
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    // ── Get escrow for booking ─────────────────────────────────
    @Transactional(readOnly = true)
    public EscrowResponse getEscrowByBooking(Long bookingId) {
        EscrowDeposit escrow = escrowRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Escrow not found for booking: " + bookingId));
        return toEscrowResponse(escrow);
    }

    // ── Admin: Get all payments ────────────────────────────────
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────
    public PaymentResponse toPaymentResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .bookingId(p.getBooking().getId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .transactionType(p.getTransactionType())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public EscrowResponse toEscrowResponse(EscrowDeposit e) {
        return EscrowResponse.builder()
                .id(e.getId())
                .bookingId(e.getBooking().getId())
                .amount(e.getAmount())
                .status(e.getStatus())
                .deductedAmount(e.getDeductedAmount())
                .deductionReason(e.getDeductionReason())
                .releasedAt(e.getReleasedAt())
                .build();
    }
}
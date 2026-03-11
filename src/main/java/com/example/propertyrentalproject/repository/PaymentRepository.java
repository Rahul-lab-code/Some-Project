package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.enums.PaymentStatus;
import com.example.propertyrentalproject.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByPayerId(Long payerId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByBookingIdAndTransactionType(
            Long bookingId, String transactionType);

    boolean existsByBookingIdAndTransactionType(
            Long bookingId, String transactionType);
}
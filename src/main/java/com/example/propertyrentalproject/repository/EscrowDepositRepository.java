package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.model.EscrowDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EscrowDepositRepository extends JpaRepository<EscrowDeposit, Long> {

    Optional<EscrowDeposit> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}
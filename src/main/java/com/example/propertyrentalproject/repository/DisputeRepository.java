package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.enums.DisputeType;
import com.example.propertyrentalproject.model.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    List<Dispute> findByRaisedById(Long userId);

    List<Dispute> findByStatus(DisputeStatus status);

    List<Dispute> findByType(DisputeType type);

    List<Dispute> findByBookingId(Long bookingId);

    boolean existsByBookingIdAndRaisedById(Long bookingId, Long userId);
}
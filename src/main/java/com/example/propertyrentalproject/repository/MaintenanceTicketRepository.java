package com.example.propertyrentalproject.repository;
 
import com.example.propertyrentalproject.enums.MaintenanceStatus;

import com.example.propertyrentalproject.model.MaintenanceTicket;

import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
 
public interface MaintenanceTicketRepository

        extends JpaRepository<MaintenanceTicket, Long> {
 
    List<MaintenanceTicket> findByPropertyId(Long propertyId);
 
    List<MaintenanceTicket> findByReportedById(Long userId);
 
    List<MaintenanceTicket> findByBookingId(Long bookingId);
 
    List<MaintenanceTicket> findByStatus(MaintenanceStatus status);
 
    List<MaintenanceTicket> findByPropertyIdAndStatus(

            Long propertyId, MaintenanceStatus status);

}
 
package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.MaintenanceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaintenanceResponse {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long bookingId;
    private Long reportedById;
    private String reportedByName;
    private String title;
    private String description;
    private MaintenanceStatus status;
    private String resolutionNote;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
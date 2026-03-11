package com.example.propertyrentalproject.dto.responses;

import com.example.propertyrentalproject.enums.DisputeStatus;
import com.example.propertyrentalproject.enums.DisputeType;
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
public class DisputeResponse {

    private Long id;
    private Long bookingId;
    private Long raisedById;
    private String raisedByName;
    private DisputeType type;
    private DisputeStatus status;
    private String description;
    private String resolution;
    private List<String> evidenceUrls;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
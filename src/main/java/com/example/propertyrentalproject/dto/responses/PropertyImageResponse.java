package com.example.propertyrentalproject.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyImageResponse {

    private Long id;
    private String imageUrl;
    private boolean isPrimary;
    private Integer sortOrder;
}
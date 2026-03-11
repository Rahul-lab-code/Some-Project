package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    private String profilePhoto;
}

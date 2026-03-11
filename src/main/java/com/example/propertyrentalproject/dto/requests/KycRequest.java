package com.example.propertyrentalproject.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KycRequest {

    @NotBlank(message = "Govt ID type is required")
    private String govtIdType;       // AADHAAR / PASSPORT / DRIVING_LICENSE

    @NotBlank(message = "Govt ID number is required")
    private String govtIdNumber;

    @NotBlank(message = "Govt ID document URL is required")
    private String govtIdDocumentUrl;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN format")
    private String panNumber;

    @NotBlank(message = "PAN document URL is required")
    private String panDocumentUrl;

    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String bankIfscCode;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account holder name is required")
    private String bankAccountHolderName;

    private String cancelledChequeUrl;
}
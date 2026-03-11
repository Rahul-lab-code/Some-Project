package com.example.propertyrentalproject.controller;

import com.example.propertyrentalproject.dto.requests.CreatePropertyRequest;
import com.example.propertyrentalproject.dto.requests.SearchPropertyRequest;
import com.example.propertyrentalproject.dto.requests.UpdatePropertyRequest;
import com.example.propertyrentalproject.dto.responses.ApiResponse;
import com.example.propertyrentalproject.dto.responses.PropertyResponse;
import com.example.propertyrentalproject.service.PropertyService;
import com.example.propertyrentalproject.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "Property listing management")
public class PropertyController {

    private final PropertyService propertyService;
    private final SecurityUtil securityUtil;

    // ── Public endpoints ───────────────────────────────────────

    @GetMapping
    @Operation(summary = "Search/browse all published properties")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> searchProperties(
            @ModelAttribute SearchPropertyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Properties fetched",
                propertyService.searchProperties(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID")
    public ResponseEntity<ApiResponse<PropertyResponse>> getProperty(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Property fetched",
                propertyService.getProperty(id)));
    }

    // ── Host endpoints ─────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Create a new property listing (HOST)")
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(
            @Valid @RequestBody CreatePropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property created",
                        propertyService.createProperty(
                                securityUtil.getCurrentUserId(), request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Update property listing (HOST)")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePropertyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Property updated",
                propertyService.updateProperty(
                        securityUtil.getCurrentUserId(), id, request)));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Publish a property (HOST)")
    public ResponseEntity<ApiResponse<PropertyResponse>> publishProperty(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Property published",
                propertyService.publishProperty(
                        securityUtil.getCurrentUserId(), id)));
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Unpublish a property (HOST)")
    public ResponseEntity<ApiResponse<PropertyResponse>> unpublishProperty(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Property unpublished",
                propertyService.unpublishProperty(
                        securityUtil.getCurrentUserId(), id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Delete (archive) a property (HOST)")
    public ResponseEntity<ApiResponse<String>> deleteProperty(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                propertyService.deleteProperty(
                        securityUtil.getCurrentUserId(), id)));
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get all my property listings (HOST)")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getMyProperties() {
        return ResponseEntity.ok(ApiResponse.success("My properties",
                propertyService.getMyProperties(
                        securityUtil.getCurrentUserId())));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all properties (ADMIN)")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success("All properties",
                propertyService.getAllProperties()));
    }
}
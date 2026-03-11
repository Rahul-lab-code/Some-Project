package com.example.propertyrentalproject.service;

import com.example.propertyrentalproject.dto.requests.CreatePropertyRequest;
import com.example.propertyrentalproject.dto.requests.SearchPropertyRequest;
import com.example.propertyrentalproject.dto.requests.UpdatePropertyRequest;
import com.example.propertyrentalproject.dto.responses.PropertyResponse;
import com.example.propertyrentalproject.enums.KycStatus;
import com.example.propertyrentalproject.enums.PropertyStatus;
import com.example.propertyrentalproject.exception.ResourceNotFoundException;
import com.example.propertyrentalproject.exception.UnauthorizedException;
import com.example.propertyrentalproject.model.Property;
import com.example.propertyrentalproject.model.PropertyImage;
import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.PropertyRepository;
import com.example.propertyrentalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;

    // ── Create property ────────────────────────────────────────
    @Transactional
    public PropertyResponse createProperty(Long hostId,
                                           CreatePropertyRequest req) {
        User host = userService.findById(hostId);

        if (!KycStatus.APPROVED.equals(host.getKycStatus()))
            throw new UnauthorizedException(
                    "KYC must be approved before listing a property");

        Property property = Property.builder()
                .host(host)
                .title(req.getTitle())
                .description(req.getDescription())
                .propertyType(req.getPropertyType())
                .status(PropertyStatus.DRAFT)
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .country(req.getCountry() != null ? req.getCountry() : "India")
                .bedrooms(req.getBedrooms())
                .bathrooms(req.getBathrooms())
                .capacity(req.getCapacity())
                .houseRules(req.getHouseRules())
                .basePrice(req.getBasePrice())
                .weeklyPrice(req.getWeeklyPrice())
                .monthlyPrice(req.getMonthlyPrice())
                .cleaningFee(req.getCleaningFee())
                .securityDeposit(req.getSecurityDeposit())
                .currency(req.getCurrency() != null ? req.getCurrency() : "INR")
                .bookingMode(req.getBookingMode())
                .cancellationPolicy(req.getCancellationPolicy())
                .minStayNights(req.getMinStayNights())
                .maxStayNights(req.getMaxStayNights())
                .images(new ArrayList<>())
                .build();

        // Add images if provided
        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
            List<PropertyImage> images = new ArrayList<>();
            for (int i = 0; i < req.getImageUrls().size(); i++) {
                PropertyImage img = PropertyImage.builder()
                        .property(property)
                        .imageUrl(req.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .sortOrder(i)
                        .build();
                images.add(img);
            }
            property.setImages(images);
        }

        propertyRepository.save(property);
        log.info("Property created: {} by host: {}", property.getId(), hostId);
        return toResponse(property);
    }

    // ── Update property ────────────────────────────────────────
    @Transactional
    public PropertyResponse updateProperty(Long hostId, Long propertyId,
                                           UpdatePropertyRequest req) {
        Property property = findById(propertyId);
        validateOwnership(property, hostId);

        if (req.getTitle()              != null) property.setTitle(req.getTitle());
        if (req.getDescription()        != null) property.setDescription(req.getDescription());
        if (req.getPropertyType()       != null) property.setPropertyType(req.getPropertyType());
        if (req.getAddressLine1()       != null) property.setAddressLine1(req.getAddressLine1());
        if (req.getAddressLine2()       != null) property.setAddressLine2(req.getAddressLine2());
        if (req.getCity()               != null) property.setCity(req.getCity());
        if (req.getState()              != null) property.setState(req.getState());
        if (req.getPincode()            != null) property.setPincode(req.getPincode());
        if (req.getCountry()            != null) property.setCountry(req.getCountry());
        if (req.getBedrooms()           != null) property.setBedrooms(req.getBedrooms());
        if (req.getBathrooms()          != null) property.setBathrooms(req.getBathrooms());
        if (req.getCapacity()           != null) property.setCapacity(req.getCapacity());
        if (req.getHouseRules()         != null) property.setHouseRules(req.getHouseRules());
        if (req.getBasePrice()          != null) property.setBasePrice(req.getBasePrice());
        if (req.getWeeklyPrice()        != null) property.setWeeklyPrice(req.getWeeklyPrice());
        if (req.getMonthlyPrice()       != null) property.setMonthlyPrice(req.getMonthlyPrice());
        if (req.getCleaningFee()        != null) property.setCleaningFee(req.getCleaningFee());
        if (req.getSecurityDeposit()    != null) property.setSecurityDeposit(req.getSecurityDeposit());
        if (req.getCurrency()           != null) property.setCurrency(req.getCurrency());
        if (req.getBookingMode()        != null) property.setBookingMode(req.getBookingMode());
        if (req.getCancellationPolicy() != null) property.setCancellationPolicy(req.getCancellationPolicy());
        if (req.getMinStayNights()      != null) property.setMinStayNights(req.getMinStayNights());
        if (req.getMaxStayNights()      != null) property.setMaxStayNights(req.getMaxStayNights());

        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
            property.getImages().clear();
            for (int i = 0; i < req.getImageUrls().size(); i++) {
                PropertyImage img = PropertyImage.builder()
                        .property(property)
                        .imageUrl(req.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .sortOrder(i)
                        .build();
                property.getImages().add(img);
            }
        }

        propertyRepository.save(property);
        log.info("Property updated: {}", propertyId);
        return toResponse(property);
    }

    // ── Publish property ───────────────────────────────────────
    @Transactional
    public PropertyResponse publishProperty(Long hostId, Long propertyId) {
        Property property = findById(propertyId);
        validateOwnership(property, hostId);

        if (property.getBasePrice() == null)
            throw new IllegalStateException("Base price must be set before publishing");
        if (property.getImages().isEmpty())
            throw new IllegalStateException("At least one image is required before publishing");

        property.setStatus(PropertyStatus.PUBLISHED);
        propertyRepository.save(property);
        log.info("Property published: {}", propertyId);
        return toResponse(property);
    }

    // ── Unpublish property ─────────────────────────────────────
    @Transactional
    public PropertyResponse unpublishProperty(Long hostId, Long propertyId) {
        Property property = findById(propertyId);
        validateOwnership(property, hostId);
        property.setStatus(PropertyStatus.UNPUBLISHED);
        propertyRepository.save(property);
        log.info("Property unpublished: {}", propertyId);
        return toResponse(property);
    }

    // ── Delete property ────────────────────────────────────────
    @Transactional
    public String deleteProperty(Long hostId, Long propertyId) {
        Property property = findById(propertyId);
        validateOwnership(property, hostId);
        property.setStatus(PropertyStatus.ARCHIVED);
        propertyRepository.save(property);
        log.info("Property archived: {}", propertyId);
        return "Property deleted successfully";
    }

    // ── Get property by ID ─────────────────────────────────────
    @Transactional(readOnly = true)
    public PropertyResponse getProperty(Long propertyId) {
        return toResponse(findById(propertyId));
    }

    // ── Get host's properties ──────────────────────────────────
    @Transactional(readOnly = true)
    public List<PropertyResponse> getMyProperties(Long hostId) {
        return propertyRepository.findByHostId(hostId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Search properties ──────────────────────────────────────
    @Transactional(readOnly = true)
    public List<PropertyResponse> searchProperties(SearchPropertyRequest req) {
        List<Property> results;

        if (req.getCheckInDate() != null && req.getCheckOutDate() != null) {
            results = propertyRepository.searchAvailableProperties(
                    req.getCity(),
                    req.getCheckInDate(),
                    req.getCheckOutDate(),
                    req.getGuests()
            );
        } else {
            results = propertyRepository.searchProperties(
                    req.getCity(),
                    req.getPropertyType(),
                    req.getMinPrice(),
                    req.getMaxPrice(),
                    req.getGuests()
            );
        }

        return results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: get all properties ──────────────────────────────
    @Transactional(readOnly = true)
    public List<PropertyResponse> getAllProperties() {
        return propertyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────
    public Property findById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Property not found: " + id));
    }

    private void validateOwnership(Property property, Long hostId) {
        if (!property.getHost().getId().equals(hostId))
            throw new UnauthorizedException(
                    "You are not the owner of this property");
    }

    public PropertyResponse toResponse(Property p) {
        List<String> imageUrls = p.getImages() == null ? new ArrayList<>() :
                p.getImages().stream()
                        .map(PropertyImage::getImageUrl)
                        .collect(Collectors.toList());

        return PropertyResponse.builder()
                .id(p.getId())
                .hostId(p.getHost().getId())
                .hostName(p.getHost().getFullName())
                .hostVerified(p.getHost().isVerificationBadge())
                .title(p.getTitle())
                .description(p.getDescription())
                .propertyType(p.getPropertyType())
                .status(p.getStatus())
                .addressLine1(p.getAddressLine1())
                .addressLine2(p.getAddressLine2())
                .city(p.getCity())
                .state(p.getState())
                .pincode(p.getPincode())
                .country(p.getCountry())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .capacity(p.getCapacity())
                .houseRules(p.getHouseRules())
                .basePrice(p.getBasePrice())
                .weeklyPrice(p.getWeeklyPrice())
                .monthlyPrice(p.getMonthlyPrice())
                .cleaningFee(p.getCleaningFee())
                .securityDeposit(p.getSecurityDeposit())
                .currency(p.getCurrency())
                .bookingMode(p.getBookingMode())
                .cancellationPolicy(p.getCancellationPolicy())
                .minStayNights(p.getMinStayNights())
                .maxStayNights(p.getMaxStayNights())
                .imageUrls(imageUrls)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
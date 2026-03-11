package com.example.propertyrentalproject.repository;

import com.example.propertyrentalproject.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

    List<PropertyImage> findByPropertyId(Long propertyId);

    void deleteByPropertyId(Long propertyId);
}
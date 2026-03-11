package com.example.propertyrentalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class PropertyRentalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyRentalProjectApplication.class, args);
    }

}

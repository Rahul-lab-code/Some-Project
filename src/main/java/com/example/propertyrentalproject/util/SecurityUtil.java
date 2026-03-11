package com.example.propertyrentalproject.util;

import com.example.propertyrentalproject.model.User;
import com.example.propertyrentalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(0L); // 0L for InMemory admin not in DB
    }

    public String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found: " + email));
    }

    // ── Check if current user is InMemory admin ────────────────
    public boolean isInMemoryAdmin() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email).isEmpty();
    }
}
package com.example.propertyrentalproject.security;

import com.example.propertyrentalproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Autowired
    private JwtUtil jwtUtil;

    public String generateTokenForUser(User user) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }
}

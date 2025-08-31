package com.example.webtoon.service;

import com.example.webtoon.dto.AuthResponse;
import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.RegisterRequest;
import com.example.webtoon.domain.User;
import com.example.webtoon.repo.UserRepository;
import com.example.webtoon.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        
        // Return complete AuthResponse with user details
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())  // ✅ Add username
                .email(user.getEmail())        // ✅ Add email
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
        
        // Return complete AuthResponse with user details
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())  // ✅ Add username
                .email(user.getEmail())        // ✅ Add email
                .build();
    }
}

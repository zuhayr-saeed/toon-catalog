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

    public AuthSession register(RegisterRequest req) {
        String username = req.getUsername().trim();
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        return new AuthSession(toResponse(user), token);
    }

    public AuthSession login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        return new AuthSession(toResponse(user), token);
    }

    public AuthResponse currentUser(User user) {
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public record AuthSession(AuthResponse response, String token) {
    }
}

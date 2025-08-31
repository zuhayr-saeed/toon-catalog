package com.example.webtoon.service;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RegisterRequest;
import com.example.webtoon.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .build();
        return userRepository.save(user);
    }
}

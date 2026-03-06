package com.example.webtoon.controller;

import com.example.webtoon.dto.AuthResponse;
import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.RegisterRequest;
import com.example.webtoon.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

private final AuthService authService;  

@PostMapping("/register")  
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {  
    return ResponseEntity.ok(authService.register(req));  
}  

@PostMapping("/login")  
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {  
    return ResponseEntity.ok(authService.login(req));  
}  

}

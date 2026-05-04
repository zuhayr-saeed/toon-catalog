package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.AuthResponse;
import com.example.webtoon.dto.LoginRequest;
import com.example.webtoon.dto.RegisterRequest;
import com.example.webtoon.security.AuthCookieService;
import com.example.webtoon.security.CsrfCookieService;
import com.example.webtoon.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final CsrfCookieService csrfCookieService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthService.AuthSession session = authService.register(req);
        return withAuthCookie(session);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthService.AuthSession session = authService.login(req);
        return withAuthCookie(session);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        String csrfToken = csrfCookieService.createToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, csrfCookieService.createCsrfCookieHeader(csrfToken))
                .body(authService.currentUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.clearAuthCookieHeader())
                .header(HttpHeaders.SET_COOKIE, csrfCookieService.clearCsrfCookieHeader())
                .build();
    }

    private ResponseEntity<AuthResponse> withAuthCookie(AuthService.AuthSession session) {
        String csrfToken = csrfCookieService.createToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.createAuthCookieHeader(session.token()))
                .header(HttpHeaders.SET_COOKIE, csrfCookieService.createCsrfCookieHeader(csrfToken))
                .body(session.response());
    }
}

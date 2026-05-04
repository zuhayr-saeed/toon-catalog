package com.example.webtoon.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieService {
    public static final String AUTH_COOKIE_NAME = "webtoon_auth";

    @Value("${app.auth-cookie.secure:false}")
    private boolean secure;

    @Value("${app.auth-cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String createAuthCookieHeader(String token) {
        return baseCookie(token)
                .maxAge(Duration.ofMillis(expirationMs))
                .build()
                .toString();
    }

    public String clearAuthCookieHeader() {
        return baseCookie("")
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/");
    }

}

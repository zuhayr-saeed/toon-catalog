package com.example.webtoon.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Component
public class CsrfCookieService {
    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.auth-cookie.secure:false}")
    private boolean secure;

    @Value("${app.auth-cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String createToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String createCsrfCookieHeader(String token) {
        return baseCookie(token)
                .maxAge(Duration.ofMillis(expirationMs))
                .build()
                .toString();
    }

    public String clearCsrfCookieHeader() {
        return baseCookie("")
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(CSRF_COOKIE_NAME, value)
                .httpOnly(false)
                .secure(secure)
                .sameSite(sameSite)
                .path("/");
    }
}

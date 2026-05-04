package com.example.webtoon.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class JwtUtil {
    private static final String DEV_DEFAULT_SECRET = "dev-webtoon-jwt-secret-change-me-0123456789";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    private Key key;

    @PostConstruct
    void init() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT secret must be configured via JWT_SECRET/app.jwt.secret");
        }

        byte[] secretBytes;
        try {
            secretBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException ignored) {
            secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        if (isProdProfile() && (DEV_DEFAULT_SECRET.equals(jwtSecret)
                || jwtSecret.startsWith("replace-with")
                || secretBytes.length < 32)) {
            throw new IllegalStateException("Production JWT_SECRET must be a unique secret with at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public Claims extractAllClaims(String token) {
        return parseClaims(token).getBody();
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private boolean isProdProfile() {
        return Stream.of(activeProfiles.split(","))
                .map(String::trim)
                .anyMatch("prod"::equalsIgnoreCase);
    }
}

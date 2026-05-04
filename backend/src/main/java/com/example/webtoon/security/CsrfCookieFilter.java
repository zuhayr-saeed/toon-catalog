package com.example.webtoon.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

@Component
public class CsrfCookieFilter extends OncePerRequestFilter {
    private static final Set<String> SAFE_METHODS = Set.of(
            HttpMethod.GET.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.TRACE.name()
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (requiresCsrf(request) && !hasValidCsrfToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresCsrf(HttpServletRequest request) {
        return !SAFE_METHODS.contains(request.getMethod())
                && request.getRequestURI().startsWith("/api/v1/")
                && !isAuthBootstrapRequest(request)
                && getCookieValue(request, AuthCookieService.AUTH_COOKIE_NAME) != null;
    }

    private boolean isAuthBootstrapRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return HttpMethod.POST.matches(request.getMethod())
                && ("/api/v1/auth/register".equals(uri) || "/api/v1/auth/login".equals(uri));
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        String cookieToken = getCookieValue(request, CsrfCookieService.CSRF_COOKIE_NAME);
        String headerToken = request.getHeader(CsrfCookieService.CSRF_HEADER_NAME);
        if (cookieToken == null || headerToken == null) {
            return false;
        }

        byte[] cookieTokenBytes = cookieToken.getBytes(StandardCharsets.UTF_8);
        byte[] headerTokenBytes = headerToken.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(cookieTokenBytes, headerTokenBytes);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName()) && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

package com.example.webtoon.config;

import com.example.webtoon.security.CsrfCookieFilter;
import com.example.webtoon.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CsrfCookieFilter csrfCookieFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .requestMatchers("/api/v1/healthz").permitAll()
                .requestMatchers("/healthz").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/v1/users/me/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/series/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/series/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/series/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/series/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ratings/*/summary").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/list").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/followers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/following").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(csrfCookieFilter, JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

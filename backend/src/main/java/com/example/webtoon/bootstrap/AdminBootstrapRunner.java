package com.example.webtoon.bootstrap;

import com.example.webtoon.domain.User;
import com.example.webtoon.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Idempotently provisions a single admin user from configuration so that a
 * brand-new deployment can be bootstrapped without manual SQL.
 *
 * Behavior:
 * - Disabled when {@code app.bootstrap.admin.username} or {@code app.bootstrap.admin.password}
 *   is blank.
 * - If the user does not exist, creates it with the configured email + password and
 *   {@code ROLE_ADMIN} (plus {@code ROLE_USER} for parity with regular signups).
 * - If the user already exists, only ensures the {@code ROLE_ADMIN} role is present.
 *   Existing passwords/emails are preserved so a re-deploy never silently overwrites them.
 */
@Component
@Slf4j
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminUsername == null || adminUsername.isBlank()) {
            return;
        }

        String username = adminUsername.trim();
        Optional<User> existing = userRepository.findByUsername(username);

        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getRoles().add("ROLE_ADMIN")) {
                userRepository.save(user);
                log.info("Bootstrap: promoted existing user '{}' to ROLE_ADMIN", username);
            } else {
                log.debug("Bootstrap: admin user '{}' already has ROLE_ADMIN", username);
            }
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("Bootstrap: admin user '{}' does not exist and no app.bootstrap.admin.password "
                    + "was provided; skipping creation", username);
            return;
        }

        String email = (adminEmail == null || adminEmail.isBlank())
                ? username + "@local"
                : adminEmail.trim().toLowerCase();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(adminPassword));
        user.getRoles().add("ROLE_USER");
        user.getRoles().add("ROLE_ADMIN");
        userRepository.save(user);

        log.info("Bootstrap: created admin user '{}'", username);
    }
}

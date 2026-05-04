package com.example.webtoon.bootstrap;

import com.example.webtoon.domain.User;
import com.example.webtoon.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminBootstrapRunnerTests {

    @SpringBootTest
    @DirtiesContext
    @TestPropertySource(properties = {
            "app.bootstrap.admin.username=zen-admin",
            "app.bootstrap.admin.email=zen@example.com",
            "app.bootstrap.admin.password=correcthorsebatterystaple"
    })
    static class CreateNewAdminTests {

        @Autowired
        UserRepository userRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        AdminBootstrapRunner runner;

        @Test
        @Transactional
        void createsAdminUserOnFirstRun() throws Exception {
            userRepository.findByUsername("zen-admin").ifPresent(userRepository::delete);
            userRepository.flush();

            runner.run(new DefaultApplicationArguments());

            User created = userRepository.findByUsername("zen-admin").orElseThrow();
            assertThat(created.getRoles()).contains("ROLE_ADMIN", "ROLE_USER");
            assertThat(created.getEmail()).isEqualTo("zen@example.com");
            assertThat(passwordEncoder.matches("correcthorsebatterystaple", created.getPassword())).isTrue();

            // Idempotent: second run does not duplicate or change credentials.
            String originalHash = created.getPassword();
            runner.run(new DefaultApplicationArguments());
            User after = userRepository.findByUsername("zen-admin").orElseThrow();
            assertThat(after.getPassword()).isEqualTo(originalHash);
            assertThat(after.getRoles()).contains("ROLE_ADMIN");
        }
    }

    @SpringBootTest
    @DirtiesContext
    @TestPropertySource(properties = {
            "app.bootstrap.admin.username=elevate-me"
    })
    static class PromoteExistingUserTests {

        @Autowired
        UserRepository userRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        AdminBootstrapRunner runner;

        @Test
        @Transactional
        void promotesExistingUserToAdminWithoutTouchingCredentials() throws Exception {
            userRepository.findByUsername("elevate-me").ifPresent(userRepository::delete);
            User existing = new User();
            existing.setUsername("elevate-me");
            existing.setEmail("elevate-" + UUID.randomUUID() + "@example.com");
            existing.setPassword(passwordEncoder.encode("their-own-password"));
            existing.getRoles().add("ROLE_USER");
            userRepository.saveAndFlush(existing);

            runner.run(new DefaultApplicationArguments());

            User after = userRepository.findByUsername("elevate-me").orElseThrow();
            assertThat(after.getRoles()).contains("ROLE_ADMIN", "ROLE_USER");
            assertThat(passwordEncoder.matches("their-own-password", after.getPassword())).isTrue();
        }
    }
}

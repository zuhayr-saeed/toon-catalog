package com.example.webtoon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Dev-only Flyway behavior: run {@code repair()} before {@code migrate()} so that
 * legitimate developer scenarios (consolidating migration files, fixing a typo in an
 * applied migration, etc.) don't brick a long-lived local database with checksum
 * mismatches. Production uses the default Spring Boot strategy and never auto-repairs.
 */
@Configuration
@Profile("dev")
@Slf4j
public class DevFlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("Dev Flyway strategy: running repair() then migrate()");
            flyway.repair();
            flyway.migrate();
        };
    }
}

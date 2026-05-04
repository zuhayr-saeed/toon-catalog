package com.example.webtoon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;

import java.util.stream.Stream;

@Configuration
public class SearchConfig {
    private static final String DEV_DEFAULT_API_KEY = "supersecret";

    @Value("${meili.host:http://localhost:7700}")
    private String meiliHost;

    @Value("${meili.apiKey:supersecret}")
    private String meiliApiKey;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Bean
    public Client meiliClient() {
        if (isProdProfile()
                && (DEV_DEFAULT_API_KEY.equals(meiliApiKey) || meiliApiKey.startsWith("replace-with"))) {
            throw new IllegalStateException("Production MEILI_API_KEY must not use the development default");
        }
        return new Client(new Config(meiliHost, meiliApiKey));
    }

    private boolean isProdProfile() {
        return Stream.of(activeProfiles.split(","))
                .map(String::trim)
                .anyMatch("prod"::equalsIgnoreCase);
    }
}

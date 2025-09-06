package com.example.webtoon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;

@Configuration
public class SearchConfig {

    @Value("${meili.host:http://localhost:7700}")
    private String meiliHost;

    @Value("${meili.apiKey:supersecret}")
    private String meiliApiKey;

    @Bean
    public Client meiliClient() {
        return new Client(new Config(meiliHost, meiliApiKey));
    }
}
package com.example.webtoon.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.redis.key-prefix:webtoon-catalog::}")
    private String redisKeyPrefix;

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper(objectMapper));
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> redisKeyPrefix + cacheName + "::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(Map.of(
                        CacheNames.SERIES_DETAIL, defaults.entryTtl(Duration.ofMinutes(15)),
                        CacheNames.SERIES_SEARCH, defaults.entryTtl(Duration.ofMinutes(5)),
                        CacheNames.RATING_SUMMARY, defaults.entryTtl(Duration.ofMinutes(5))
                ))
                .transactionAware()
                .build();
    }

    private ObjectMapper redisObjectMapper(ObjectMapper objectMapper) {
        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("com.example.webtoon.dto")
                .allowIfSubType("java.util")
                .allowIfSubType("java.time")
                .build();

        return objectMapper.copy()
                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }
}

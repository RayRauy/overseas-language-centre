package com.school_management.overseas_language_centre.feature.integration.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{
    private static final Duration DEFAULT_TTL = Duration.ofHours(8);
    private final StringRedisTemplate redisTemplate;
    @Override
    public void save(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public void save(String key, String value) {
        save(key, value, DEFAULT_TTL);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean remove(String key) {
        Long deleted = redisTemplate.delete(Collections.singleton(key));
        return Optional.ofNullable(deleted).orElse(0L) > 0;
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

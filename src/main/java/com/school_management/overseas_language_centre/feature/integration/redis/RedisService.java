package com.school_management.overseas_language_centre.feature.integration.redis;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {
    void save(String key, String value, Duration ttl);
    void save(String key, String value);
    Optional<String> get(String key);
    boolean remove(String key);
    boolean exists(String key);
}

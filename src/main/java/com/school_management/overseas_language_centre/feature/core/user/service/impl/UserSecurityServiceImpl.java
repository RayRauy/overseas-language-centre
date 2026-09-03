package com.school_management.overseas_language_centre.feature.core.user.service.impl;

import com.school_management.overseas_language_centre.entity.Permission;
import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserSecurityData;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.core.user.service.UserSecurityService;
import com.school_management.overseas_language_centre.feature.integration.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserSecurityServiceImpl implements UserSecurityService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private static final String USER_SECURITY_DATA_KEY_PREFIX = "user_security:";

    @Override
    public UserSecurityData getUserSecurityData(String username) {
        String key = USER_SECURITY_DATA_KEY_PREFIX + username;
        String cachedData = redisService.get(key).orElse(null);

        // Checks redis
        if(cachedData != null) {
            return objectMapper.readValue(cachedData, UserSecurityData.class);
        }

        // If not in redis, check in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found"));

        UserSecurityData securityData = buildUserSecurityData(user);

        // Cache user security data to Redis
        redisService.save(key, objectMapper.writeValueAsString(securityData));

        return securityData;
    }

    @Override
    public void refreshUserSecurityData(User user, String activeTokenId) {
        UserSecurityData securityData = new UserSecurityData(
                activeTokenId,
                buildAuthorities(user)
        );

        String key = USER_SECURITY_DATA_KEY_PREFIX + user.getUsername();
        redisService.save(
                key,
                objectMapper.writeValueAsString(securityData)
        );
    }

    private UserSecurityData buildUserSecurityData(User user) {
        return new UserSecurityData(
                user.getActiveTokenId(),
                buildAuthorities(user)
        );
    }

    private List<String> buildAuthorities(User user) {
        return user.getRoles()
                .stream()
                .flatMap(role -> Stream.concat(
                        Stream.of("ROLE_" + role.getName()),
                        role.getPermissions()
                                .stream()
                                .map(Permission::getName)
                ))
                .distinct()
                .toList();
    }
}

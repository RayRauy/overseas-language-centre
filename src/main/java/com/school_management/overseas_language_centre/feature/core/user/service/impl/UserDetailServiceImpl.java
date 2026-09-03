package com.school_management.overseas_language_centre.feature.core.user.service.impl;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.detail.CustomUserDetail;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserDetails;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserSecurityData;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.core.user.service.UserDetailService;
import com.school_management.overseas_language_centre.feature.core.user.service.UserSecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;
    private final UserSecurityService userSecurityService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Get user from Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Get authorities from Redis or Database
        UserSecurityData securityData = userSecurityService.getUserSecurityData(username);

        // Now convert type String to type GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = securityData.authorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new CustomUserDetail(user, authorities);
    }
}

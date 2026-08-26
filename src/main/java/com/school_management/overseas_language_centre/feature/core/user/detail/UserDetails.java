package com.school_management.overseas_language_centre.feature.core.user.detail;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserDetails {
    Collection<? extends GrantedAuthority> getAuthorities();
    String getUsername();
    String getPassword();
}

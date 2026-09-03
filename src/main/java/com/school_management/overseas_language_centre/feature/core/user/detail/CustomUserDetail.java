package com.school_management.overseas_language_centre.feature.core.user.detail;

import com.school_management.overseas_language_centre.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails{
    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    public CustomUserDetail(
            User user,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        // Role authorities
//        authorities.addAll(user.getRoles()
//                .stream()
//                .map(role -> new
//                        SimpleGrantedAuthority("ROLE_"+ role.getName())
//                )
//                .toList()
//        );
//
//        // Permission authorities
//        authorities.addAll(user.getRoles()
//                .stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .map(permission -> new
//                        SimpleGrantedAuthority(permission.getName())
//                )
//                .toList()
//        );
        return authorities;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

}

package com.school_management.overseas_language_centre.security;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.detail.CustomUserDetail;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserDetails;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserSecurityData;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.core.user.service.UserSecurityService;
import com.school_management.overseas_language_centre.feature.core.user.service.impl.UserDetailServiceImpl;
import com.school_management.overseas_language_centre.feature.integration.redis.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserSecurityService userSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("Request");
        System.out.println(request);
        String token = resolveToken(request);
        System.out.println("token");
        System.out.println(token);

        if (token == null || token.isBlank() || !jwtService.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }
        // Optional
        Claims claims = jwtService.getClaimsFromToken(token);

        String username = jwtService.getUsernameFromToken(token);
        String jti = jwtService.getJtiFromToken(token);

        UserSecurityData securityData = userSecurityService.getUserSecurityData(username);

        if(securityData == null){
            chain.doFilter(request, response);
            return;
        }

//        if(!isCurrentSession(username, token)){
//            chain.doFilter(request, response);
//            return;
//        }
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (!jti.equals(securityData.activeTokenId())) {
            SecurityContextHolder.clearContext();

            restAuthenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Token has been revoked")
            );
            return;
        }

        Collection<? extends GrantedAuthority> authorities = securityData.authorities().stream().map(SimpleGrantedAuthority::new).toList();

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new CustomUserDetail(user, authorities);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);

    }
    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println(authHeader);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

//    private boolean isCurrentSession(String username, String token){
//        try{
//            Optional<String> store = redisService.get(TOKEN_KEY_PREFIX + username);
//            return store.isPresent() && store.get().equals(token);
//        }
//        catch (Exception e){
//            return false;
//        }
//    }
}

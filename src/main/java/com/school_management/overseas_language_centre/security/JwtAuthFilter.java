package com.school_management.overseas_language_centre.security;

import com.school_management.overseas_language_centre.feature.core.user.detail.UserDetails;
import com.school_management.overseas_language_centre.feature.core.user.service.impl.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final UserDetailServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInR5cGUiOiJhY2Nlc3MiLCJqdGkiOiI4NGUzMTRhMy00YTNiLTQ1ODktYTFhZi00ZTA2NjJiY2U2NzUiLCJpYXQiOjE3ODc2NzA3MTgsImV4cCI6MTc4Nzc1NzExOH0.IxGVEyPIfRNLfMOF1kIv5UFm1JFVUCi4-HxJ0eGaiX8Y5X70Zozp-iti3yCtbtl68YdrRqkrwPt8XAbB6TgQpw
        System.out.println("Request");
        System.out.println(request);
        String token = resolveToken(request);
        System.out.println("token");
        System.out.println(token);

        if (token == null || token.isBlank() || !jwtService.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }
        String username = jwtService.getUsernameFromToken(token);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
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
}

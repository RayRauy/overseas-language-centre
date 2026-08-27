package com.school_management.overseas_language_centre.security;

import com.school_management.overseas_language_centre.property.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;

    public Claims getClaimsFromToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)     // verify signature before reading claims
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String generateToken(String username){

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(username)
                .id(jti)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {

        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Duration getExpirationDuration() {
        return Duration.ofMillis(jwtProperties.getExpirationMs());
    }

    public String getJtiFromToken(String token) {
        return getClaimsFromToken(token).getId();
    }
}

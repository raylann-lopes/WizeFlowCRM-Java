package com.wizeflow.crm.backend.security.service;

import com.wizeflow.crm.backend.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {


    private final JwtProperties jwtProperties;


    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("authorities", userDetails.getAuthorities());


        return createToken(claims, userDetails.getUsername(), jwtProperties.getExpiration());
    }


    public String generateRefreshToken(UserDetails userDetails) {


        return createToken(new HashMap<>(), userDetails.getUsername(), jwtProperties.getRefreshExpiration());
    }


    private String createToken(Map<String, Object> claims, String subject, Long expiration) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expiration);


        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }


    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


    private Boolean isTokenExpired(String token) {


        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        try {

            final String username = extractUsername(token);





            if (username == null || userDetails == null) {
                return false;
            }


            if (!userDetails.isEnabled()
                    || !userDetails.isAccountNonLocked()
                    || !userDetails.isAccountNonExpired()
                    || !userDetails.isCredentialsNonExpired()) {
                return false;
            }

            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

        } catch (Exception e) {


            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    private SecretKey getSigningKey() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret não configurado");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}

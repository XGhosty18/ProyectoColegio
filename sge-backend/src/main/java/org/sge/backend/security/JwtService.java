package org.sge.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(String username, List<String> roles, List<String> permissions) {
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .claim("permissions", permissions)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        var roles = extractClaims(token).get("roles");
        if (roles instanceof List<?>) {
            return (List<String>) roles;
        }
        // Backward compatibility: single "role" claim
        var singleRole = extractClaims(token).get("role", String.class);
        return singleRole != null ? List.of(singleRole) : List.of();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        var perms = extractClaims(token).get("permissions");
        if (perms instanceof List<?>) {
            return (List<String>) perms;
        }
        return List.of();
    }

    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}

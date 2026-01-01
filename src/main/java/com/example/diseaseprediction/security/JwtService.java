package com.example.diseaseprediction.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret:change-me-secret-key-change-me}")
    private String secret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = getAllClaims(token);
        Object roleObj = claims.get("role");
        return roleObj != null ? roleObj.toString() : null;
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = decodeSecret(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] decodeSecret(String rawSecret) {
        String trimmed = rawSecret == null ? "" : rawSecret.trim();

        // Prefer base64-decoded secret when valid and long enough; otherwise use raw bytes.
        byte[] candidate = tryDecodeBase64(trimmed);
        if (candidate == null) {
            candidate = trimmed.getBytes(StandardCharsets.UTF_8);
        }

        if (candidate.length < 32) { // 256 bits minimum for HS256
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes). Update app.jwt.secret.");
        }
        return candidate;
    }

    private byte[] tryDecodeBase64(String value) {
        try {
            byte[] decoded = Decoders.BASE64.decode(value);
            return decoded.length >= 32 ? decoded : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

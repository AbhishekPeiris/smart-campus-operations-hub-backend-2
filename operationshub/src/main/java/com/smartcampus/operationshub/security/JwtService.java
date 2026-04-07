package com.smartcampus.operationshub.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = resolveSecretKeyBytes(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] resolveSecretKeyBytes(String secret) {
        byte[] rawSecretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (rawSecretBytes.length >= 32) {
            return rawSecretBytes;
        }

        byte[] base64Decoded = tryDecodeBase64(secret);
        if (base64Decoded != null && base64Decoded.length >= 32) {
            return base64Decoded;
        }

        byte[] base64UrlDecoded = tryDecodeBase64Url(secret);
        if (base64UrlDecoded != null && base64UrlDecoded.length >= 32) {
            return base64UrlDecoded;
        }

        throw new IllegalStateException("JWT secret key must be at least 32 bytes for HS256");
    }

    private byte[] tryDecodeBase64(String secret) {
        try {
            return Decoders.BASE64.decode(secret);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private byte[] tryDecodeBase64Url(String secret) {
        try {
            return Decoders.BASE64URL.decode(secret);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
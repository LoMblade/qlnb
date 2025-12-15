package com.example.admin_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /* ===================== CORE ===================== */

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /* ===================== GENERATE ===================== */

    public String generateToken(
            String username,
            String roleCode,
            String departmentCode
    ) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", roleCode)
                .claim("department", departmentCode)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username, String roleCode) {
        return generateToken(username, roleCode, null);
    }

    /* ===================== PARSE ===================== */

    public String getUsernameFromJWT(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromJWT(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String getDepartmentFromJWT(String token) {
        return parseClaims(token).get("department", String.class);
    }

    /* ===================== VALIDATE ===================== */

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /* ===================== INTERNAL ===================== */

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

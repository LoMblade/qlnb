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
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    /* ===================== CONSTANT ===================== */

    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    /* ===================== CORE ===================== */

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /* ===================== GENERATE ===================== */

    /** ACCESS TOKEN */
    public String generateAccessToken(
            String username,
            String roleCode,
            String departmentCode
    ) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", roleCode)
                .claim("department", departmentCode)
                .claim("type", TOKEN_TYPE_ACCESS)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** REFRESH TOKEN */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("type", TOKEN_TYPE_REFRESH)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ===================== BACKWARD COMPAT ===================== */
    /** Giữ lại cho code cũ nếu đang gọi */
    public String generateToken(String username, String roleCode, String departmentCode) {
        return generateAccessToken(username, roleCode, departmentCode);
    }

    public String generateToken(String username, String roleCode) {
        return generateAccessToken(username, roleCode, null);
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

    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
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

    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(getTokenType(token));
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

package com.example.admin_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Secret key dùng để ký và verify JWT (lấy từ application.yml)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Thời gian sống của access token (ms)
    @Value("${jwt.expiration}")
    private long accessTokenExpirationMs;

    // Thời gian sống của refresh token (ms)
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    // Định danh loại token
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    // Tạo key ký JWT từ secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Sinh access token
    public String generateAccessToken(
            String username,
            String roleCode,
            String departmentCode
    ) {
        return Jwts.builder()
                .setSubject(username)                  // subject = username
                .claim("role", roleCode)               // gắn role vào token
                .claim("department", departmentCode)   // gắn department
                .claim("type", TOKEN_TYPE_ACCESS)      // đánh dấu ACCESS token
                .setIssuedAt(new Date())               // thời điểm tạo
                .setExpiration(
                        new Date(System.currentTimeMillis() + accessTokenExpirationMs)
                )                                      // thời điểm hết hạn
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Sinh refresh token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("type", TOKEN_TYPE_REFRESH)     // đánh dấu REFRESH token
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + refreshTokenExpirationMs)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Overload tiện dùng
    public String generateToken(String username, String roleCode, String departmentCode) {
        return generateAccessToken(username, roleCode, departmentCode);
    }

    public String generateToken(String username, String roleCode) {
        return generateAccessToken(username, roleCode, null);
    }

    // Lấy username từ token
    public String getUsernameFromJWT(String token) {
        return parseClaims(token).getSubject();
    }

    // Lấy role từ token
    public String getRoleFromJWT(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // Lấy department từ token
    public String getDepartmentFromJWT(String token) {
        return parseClaims(token).get("department", String.class);
    }

    // Lấy loại token (ACCESS / REFRESH)
    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }
    
    // Kiểm tra token hợp lệ (đúng chữ ký, chưa hết hạn)
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // Check access token
    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    // Check refresh token
    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(getTokenType(token));
    }

    // Parse JWT và verify chữ ký
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


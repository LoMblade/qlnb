package com.example.admin_service.auth.service;

import com.example.admin_service.auth.entity.RefreshToken;
import com.example.admin_service.auth.repository.RefreshTokenRepository;
import com.example.admin_service.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken createToken(String username) {
        repo.deleteByUsername(username);

        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();

        return repo.save(refresh);
    }

    public RefreshToken validate(String token) {
        RefreshToken refresh = repo.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        if (refresh.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refresh);
            throw new TokenRefreshException("Refresh token expired");
        }

        return refresh;
    }
}

package com.example.admin_service.auth.service;

import com.example.admin_service.auth.entity.RefreshToken;
import com.example.admin_service.auth.repository.RefreshTokenRepository;
import com.example.admin_service.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

// tạo mới, lưu và check tính hợp lệ của token
@Service
public class RefreshTokenService {

    // Thời gian tồn tại của refresh token (ms), được cấu hình từ application.properties
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository repo;

    /**
     * Constructor inject repository
     */
    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    // tạo mới refreshtoken
    public RefreshToken createToken(String username) {

        // Tạo token ngẫu nhiên và set thời gian hết hạn
        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString()) // token ngẫu nhiên
                .expiryDate(Instant.now().plusMillis(refreshExpiration)) // thời gian hết hạn
                .build();

        // Lưu token vào DB và trả về
        return repo.save(refresh);
    }

    // kiểm tra tính hợp lệ cuả token
    public RefreshToken validate(String token) {
        // Lấy token từ DB, nếu không tồn tại ném exception
        RefreshToken refresh = repo.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        // Kiểm tra thời gian hết hạn
        if (refresh.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refresh); // xóa token đã hết hạn
            throw new TokenRefreshException("Refresh token expired");
        }

        return refresh;
    }
}

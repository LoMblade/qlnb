package com.example.admin_service.auth.service;

import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import com.example.admin_service.auth.dto.*;
import com.example.admin_service.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor cho AuthService, inject các dependency cần thiết.
     */
    public AuthService(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }


    public TokenResponse login(LoginRequest req) {

        // Log thông tin login attempt
        logger.info("Login attempt: {}", req.getUsername());

        // Lấy user từ DB theo username, nếu không tồn tại ném lỗi UNAUTHORIZED
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        )
                );

        // Kiểm tra trạng thái active của user
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new DisabledException("User is inactive");
        }

        // Lấy roleCode và departmentCode nếu có
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        String departmentCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;

        // Tạo access token và refresh token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), roleCode, departmentCode);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // Trả về TokenResponse
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }


    public TokenResponse refresh(String refreshToken) {

        // Kiểm tra tính hợp lệ và loại token
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // Lấy username từ refresh token
        String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);

        // Lấy user từ DB theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
                );

        // Lấy roleCode và departmentCode
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        String departmentCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;

        // Tạo access token và refresh token mới
        String newAccessToken = jwtTokenProvider.generateAccessToken(username, roleCode, departmentCode);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    public TokenResponse register(RegisterRequest req) {

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra email đã tồn tại nếu email được cung cấp
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Tạo user mới và mã hóa password
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .active(true) // mặc định active
                .build();

        userRepository.save(user);

        // Tạo access token và refresh token cho user mới
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), null, null);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}

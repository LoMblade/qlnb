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

    public AuthService(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /* ===================== LOGIN ===================== */

    public TokenResponse login(LoginRequest req) {

        logger.info("Login attempt: {}", req.getUsername());

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        )
                );

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new DisabledException("User is inactive");
        }

        String roleCode =
                user.getRole() != null ? user.getRole().getRoleCode() : null;

        String departmentCode =
                user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;

        String accessToken =
                jwtTokenProvider.generateAccessToken(
                        user.getUsername(),
                        roleCode,
                        departmentCode
                );

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    /* ===================== REFRESH ===================== */

    public TokenResponse refresh(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)
                || !jwtTokenProvider.isRefreshToken(refreshToken)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );

        String roleCode =
                user.getRole() != null ? user.getRole().getRoleCode() : null;

        String departmentCode =
                user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;

        String newAccessToken =
                jwtTokenProvider.generateAccessToken(
                        username,
                        roleCode,
                        departmentCode
                );

        String newRefreshToken =
                jwtTokenProvider.generateRefreshToken(username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    /* ===================== REGISTER ===================== */

    public TokenResponse register(RegisterRequest req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .active(true)
                .build();

        userRepository.save(user);

        String accessToken =
                jwtTokenProvider.generateAccessToken(
                        user.getUsername(),
                        null,
                        null
                );

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}

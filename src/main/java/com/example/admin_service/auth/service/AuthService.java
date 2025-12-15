package com.example.admin_service.auth.service;

import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import com.example.admin_service.auth.dto.*;
import com.example.admin_service.auth.entity.RefreshToken;
import com.example.admin_service.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authManager,
                       UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenService refreshTokenService,
                       PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse login(LoginRequest req) {
        logger.info("Attempting to login user: {}", req.getUsername());

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(),
                            req.getPassword()
                    )
            );

            User user = userRepository.findByUsername(req.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (Boolean.FALSE.equals(user.getActive())) {
                throw new DisabledException("User is inactive");
            }

            String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
            String departmentCode = user.getDepartment() != null
                    ? user.getDepartment().getDepartmentCode()
                    : null;

            String accessToken =
                    jwtTokenProvider.generateToken(user.getUsername(), roleCode, departmentCode);

            RefreshToken refreshToken =
                    refreshTokenService.createToken(user.getUsername());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();

        } catch (BadCredentialsException |
                 UsernameNotFoundException |
                 DisabledException ex) {

            logger.warn("Login failed for user {}: {}", req.getUsername(), ex.getMessage());

            // LOGIN FAIL → 401 (ĐÚNG CHUẨN)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }
    }

    // ====== REFRESH & REGISTER giữ nguyên ======

    public TokenResponse refresh(String oldToken) {
        RefreshToken refresh = refreshTokenService.validate(oldToken);

        User user = userRepository.findByUsername(refresh.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        String departmentCode = user.getDepartment() != null
                ? user.getDepartment().getDepartmentCode()
                : null;

        String newJwt = jwtTokenProvider.generateToken(user.getUsername(), roleCode, departmentCode);
        RefreshToken newRefresh = refreshTokenService.createToken(user.getUsername());

        return TokenResponse.builder()
                .accessToken(newJwt)
                .refreshToken(newRefresh.getToken())
                .build();
    }

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

        String jwt = jwtTokenProvider.generateToken(user.getUsername(), null, null);
        RefreshToken refresh = refreshTokenService.createToken(user.getUsername());

        return TokenResponse.builder()
                .accessToken(jwt)
                .refreshToken(refresh.getToken())
                .build();
    }
}


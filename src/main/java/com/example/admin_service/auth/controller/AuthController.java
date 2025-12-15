package com.example.admin_service.auth.controller;

import com.example.admin_service.auth.dto.*;
import com.example.admin_service.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest req) {
        return service.refresh(req.getRefreshToken());
    }

    @PostMapping("/register")
    public TokenResponse register(@RequestBody com.example.admin_service.auth.dto.RegisterRequest req) {
        return service.register(req);
    }
}

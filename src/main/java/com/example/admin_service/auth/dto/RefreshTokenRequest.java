package com.example.admin_service.auth.dto;

import lombok.*;

@Getter @Setter
public class RefreshTokenRequest {
    private String refreshToken;
}

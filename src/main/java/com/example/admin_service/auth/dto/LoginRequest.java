package com.example.admin_service.auth.dto;

import lombok.*;

@Getter @Setter
public class LoginRequest {
    private String username;
    private String password;
}

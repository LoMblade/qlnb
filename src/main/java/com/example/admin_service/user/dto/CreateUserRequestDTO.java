package com.example.admin_service.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private String roleCode;
    private String departmentCode;

    @NotBlank
    private String password;
}

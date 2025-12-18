package com.example.admin_service.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDTO {

    private String username;
    private String email;
    private String password;

    private String roleCode;
    private String departmentCode;

    private Boolean active;
}

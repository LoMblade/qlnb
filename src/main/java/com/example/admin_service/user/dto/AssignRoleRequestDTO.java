package com.example.admin_service.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequestDTO {
    
    @NotBlank(message = "Role code is required")
    private String roleCode;

    @NotBlank(message = "Department code is required")
    private String departmentCode;
}


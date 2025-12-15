package com.example.admin_service.rolepermission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequestDTO {
    
    @NotBlank(message = "Role code is required")
    private String roleCode;

    @NotEmpty(message = "At least one permission code is required")
    private List<String> permissionCodes;
}


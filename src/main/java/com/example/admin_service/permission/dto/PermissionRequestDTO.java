package com.example.admin_service.permission.dto;

import com.example.admin_service.permission.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionRequestDTO {
    
    @NotBlank(message = "Permission code is required")
    private String permissionCode;

    @NotBlank(message = "Permission name is required")
    private String name;

    private String description;

    @NotNull(message = "Resource type is required")
    private Permission.ResourceType resourceType;

    @NotNull(message = "Action type is required")
    private Permission.ActionType actionType;
}


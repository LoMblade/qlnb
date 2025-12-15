package com.example.admin_service.permission.dto;

import com.example.admin_service.permission.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
    private Long id;
    private String permissionCode;
    private String name;
    private String description;
    private Permission.ResourceType resourceType;
    private Permission.ActionType actionType;
}


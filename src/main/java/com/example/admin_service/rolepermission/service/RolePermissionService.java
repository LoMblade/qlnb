package com.example.admin_service.rolepermission.service;

import com.example.admin_service.rolepermission.dto.AssignPermissionRequestDTO;

import java.util.List;

public interface RolePermissionService {
    void assignPermissionsToRole(AssignPermissionRequestDTO dto);
    void removePermissionFromRole(String roleCode, String permissionCode);
    List<String> getPermissionsByRoleCode(String roleCode);
    boolean hasPermission(String roleCode, String permissionCode);
    boolean hasPermission(String roleCode, String resourceType, String actionType);
}


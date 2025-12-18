package com.example.admin_service.rolepermission.service;

import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.rolepermission.dto.AssignPermissionRequestDTO;

import java.util.List;

public interface RolePermissionService {

    // Gán danh sách permission cho role
    void assignPermissionsToRole(AssignPermissionRequestDTO dto);

    // Xóa 1 permission khỏi role (theo resource + action)
    void removePermissionFromRole(
            String roleCode,
            Permission.ResourceType resourceType,
            Permission.ActionType actionType
    );

    // Lấy danh sách permissionCode của role
    List<String> getPermissionsByRoleCode(String roleCode);

    // Check permission theo permissionCode
    boolean hasPermission(String roleCode, String permissionCode);

    // Check permission theo resource + action
    boolean hasPermission(String roleCode, String resourceType, String actionType);
}

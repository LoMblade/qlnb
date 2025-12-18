package com.example.admin_service.rolepermission.service;

import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.permission.repository.PermissionRepository;
import com.example.admin_service.role.entity.Role;
import com.example.admin_service.role.repository.RoleRepository;
import com.example.admin_service.rolepermission.dto.AssignPermissionRequestDTO;
import com.example.admin_service.rolepermission.entity.RolePermission;
import com.example.admin_service.rolepermission.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void assignPermissionsToRole(AssignPermissionRequestDTO dto) {

        Role role = roleRepository.findByRoleCode(dto.getRoleCode())
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + dto.getRoleCode())
                );

        // Xóa toàn bộ permission cũ của role
        rolePermissionRepository.deleteByRoleId(role.getId());

        // Gán lại permission mới
        for (String permissionCode : dto.getPermissionCodes()) {

            Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                    .orElseThrow(() ->
                            new RuntimeException("Permission not found: " + permissionCode)
                    );

            RolePermission rolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();

            rolePermissionRepository.save(rolePermission);
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(
            String roleCode,
            Permission.ResourceType resourceType,
            Permission.ActionType actionType
    ) {
        // Build permissionCode theo chuẩn RESOURCE_ACTION
        String permissionCode = resourceType.name() + "_" + actionType.name();

        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleCode)
                );

        Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() ->
                        new RuntimeException("Permission not found: " + permissionCode)
                );

        RolePermission rolePermission =
                rolePermissionRepository
                        .findByRoleAndPermission(role, permission)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission " + permissionCode +
                                                " not assigned to role " + roleCode
                                )
                        );

        rolePermissionRepository.delete(rolePermission);
    }

    @Override
    public List<String> getPermissionsByRoleCode(String roleCode) {
        return rolePermissionRepository.findByRoleCode(roleCode).stream()
                .map(rp -> rp.getPermission().getPermissionCode())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(String roleCode, String permissionCode) {
        return rolePermissionRepository
                .findByRoleCodeAndPermissionCode(roleCode, permissionCode)
                .isPresent();
    }

    @Override
    public boolean hasPermission(
            String roleCode,
            String resourceType,
            String actionType
    ) {
        Permission.ResourceType resType =
                Permission.ResourceType.valueOf(resourceType.toUpperCase());

        Permission.ActionType actType =
                Permission.ActionType.valueOf(actionType.toUpperCase());

        return rolePermissionRepository.findByRoleCode(roleCode).stream()
                .anyMatch(rp ->
                        rp.getPermission().getResourceType() == resType &&
                                rp.getPermission().getActionType() == actType
                );
    }
}

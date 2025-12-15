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
                .orElseThrow(() -> new RuntimeException("Role not found with code: " + dto.getRoleCode()));

        // Remove existing permissions for this role
        rolePermissionRepository.deleteByRoleId(role.getId());

        // Assign new permissions
        for (String permissionCode : dto.getPermissionCodes()) {
            Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                    .orElseThrow(() -> new RuntimeException("Permission not found with code: " + permissionCode));

            RolePermission rolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();

            rolePermissionRepository.save(rolePermission);
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(String roleCode, String permissionCode) {
        RolePermission rolePermission = rolePermissionRepository
                .findByRoleCodeAndPermissionCode(roleCode, permissionCode)
                .orElseThrow(() -> new RuntimeException("Role permission not found"));

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
        return rolePermissionRepository.findByRoleCodeAndPermissionCode(roleCode, permissionCode)
                .isPresent();
    }

    @Override
    public boolean hasPermission(String roleCode, String resourceType, String actionType) {
        Permission.ResourceType resType = Permission.ResourceType.valueOf(resourceType.toUpperCase());
        Permission.ActionType actType = Permission.ActionType.valueOf(actionType.toUpperCase());

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleCode(roleCode);
        return rolePermissions.stream()
                .anyMatch(rp -> rp.getPermission().getResourceType() == resType &&
                               rp.getPermission().getActionType() == actType);
    }
}


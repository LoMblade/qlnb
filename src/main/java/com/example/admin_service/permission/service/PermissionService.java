package com.example.admin_service.permission.service;

import com.example.admin_service.permission.dto.PermissionRequestDTO;
import com.example.admin_service.permission.dto.PermissionResponseDTO;
import com.example.admin_service.permission.entity.Permission;

import java.util.List;

public interface PermissionService {

    PermissionResponseDTO createPermission(PermissionRequestDTO dto);

    PermissionResponseDTO getPermissionById(Long id);

    PermissionResponseDTO getPermissionByCode(String permissionCode);

    List<PermissionResponseDTO> getAllPermissions();

    List<PermissionResponseDTO> getPermissionsByResourceType(
            Permission.ResourceType resourceType
    );

    PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO dto);

    void deletePermission(Long id);
}


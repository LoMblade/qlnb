package com.example.admin_service.role.service;

import com.example.admin_service.role.dto.RoleRequestDTO;
import com.example.admin_service.role.dto.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    RoleResponseDTO createRole(RoleRequestDTO dto);
    RoleResponseDTO getRoleById(Long id);
    RoleResponseDTO getRoleByCode(String roleCode);
    List<RoleResponseDTO> getAllRoles();
    List<RoleResponseDTO> getRolesHierarchy();
    RoleResponseDTO updateRole(Long id, RoleRequestDTO dto);
    void deleteRole(Long id);
}


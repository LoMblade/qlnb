package com.example.admin_service.permission.service;

import com.example.admin_service.permission.dto.PermissionRequestDTO;
import com.example.admin_service.permission.dto.PermissionResponseDTO;
import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public PermissionResponseDTO createPermission(PermissionRequestDTO dto) {
        if (permissionRepository.existsByPermissionCode(dto.getPermissionCode())) {
            throw new RuntimeException("Permission code already exists: " + dto.getPermissionCode());
        }

        Permission permission = Permission.builder()
                .permissionCode(dto.getPermissionCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .resourceType(dto.getResourceType())
                .actionType(dto.getActionType())
                .build();

        permission = permissionRepository.save(permission);
        return mapToDTO(permission);
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        return mapToDTO(permission);
    }

    @Override
    public PermissionResponseDTO getPermissionByCode(String permissionCode) {
        Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() -> new RuntimeException("Permission not found with code: " + permissionCode));
        return mapToDTO(permission);
    }

    @Override
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionResponseDTO> getPermissionsByResourceType(String resourceType) {
        Permission.ResourceType type = Permission.ResourceType.valueOf(resourceType.toUpperCase());
        return permissionRepository.findByResourceType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO dto) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));

        if (!permission.getPermissionCode().equals(dto.getPermissionCode()) &&
            permissionRepository.existsByPermissionCode(dto.getPermissionCode())) {
            throw new RuntimeException("Permission code already exists: " + dto.getPermissionCode());
        }

        permission.setPermissionCode(dto.getPermissionCode());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        permission.setResourceType(dto.getResourceType());
        permission.setActionType(dto.getActionType());

        permission = permissionRepository.save(permission);
        return mapToDTO(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponseDTO mapToDTO(Permission permission) {
        return PermissionResponseDTO.builder()
                .id(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .resourceType(permission.getResourceType())
                .actionType(permission.getActionType())
                .build();
    }
}


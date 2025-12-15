package com.example.admin_service.role.service;

import com.example.admin_service.role.dto.RoleRequestDTO;
import com.example.admin_service.role.dto.RoleResponseDTO;
import com.example.admin_service.role.entity.Role;
import com.example.admin_service.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        if (roleRepository.existsByRoleCode(dto.getRoleCode())) {
            throw new RuntimeException("Role code already exists: " + dto.getRoleCode());
        }

        Role parent = null;
        if (dto.getParentRoleCode() != null && !dto.getParentRoleCode().isEmpty()) {
            parent = roleRepository.findByRoleCode(dto.getParentRoleCode())
                    .orElseThrow(() -> new RuntimeException("Parent role not found with code: " + dto.getParentRoleCode()));
        }

        Role role = Role.builder()
                .roleCode(dto.getRoleCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .parent(parent)
                .build();

        role = roleRepository.save(role);
        return mapToDTO(role);
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return mapToDTO(role);
    }

    @Override
    public RoleResponseDTO getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new RuntimeException("Role not found with code: " + roleCode));
        return mapToDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponseDTO> getRolesHierarchy() {
        List<Role> rootRoles = roleRepository.findRootRoles();
        return rootRoles.stream()
                .map(this::mapToDTOWithChildren)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        if (!role.getRoleCode().equals(dto.getRoleCode()) &&
            roleRepository.existsByRoleCode(dto.getRoleCode())) {
            throw new RuntimeException("Role code already exists: " + dto.getRoleCode());
        }

        Role parent = null;
        if (dto.getParentRoleCode() != null && !dto.getParentRoleCode().isEmpty()) {
            parent = roleRepository.findByRoleCode(dto.getParentRoleCode())
                    .orElseThrow(() -> new RuntimeException("Parent role not found with code: " + dto.getParentRoleCode()));
        }

        role.setRoleCode(dto.getRoleCode());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setParent(parent);

        role = roleRepository.save(role);
        return mapToDTO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    private RoleResponseDTO mapToDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .name(role.getName())
                .description(role.getDescription())
                .parentRoleCode(role.getParent() != null ? role.getParent().getRoleCode() : null)
                .parentRoleName(role.getParent() != null ? role.getParent().getName() : null)
                .build();
    }

    private RoleResponseDTO mapToDTOWithChildren(Role role) {
        RoleResponseDTO dto = mapToDTO(role);
        if (role.getChildren() != null && !role.getChildren().isEmpty()) {
            List<RoleResponseDTO> children = role.getChildren().stream()
                    .map(this::mapToDTOWithChildren)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }
}


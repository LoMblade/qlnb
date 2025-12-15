package com.example.admin_service.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    private Long id;
    private String roleCode;
    private String name;
    private String description;
    private String parentRoleCode;
    private String parentRoleName;
    private List<RoleResponseDTO> children;
}


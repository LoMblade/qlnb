package com.example.admin_service.role.controller;

import com.example.admin_service.role.dto.RoleRequestDTO;
import com.example.admin_service.role.dto.RoleResponseDTO;
import com.example.admin_service.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/code/{roleCode}")
    public ResponseEntity<RoleResponseDTO> getRoleByCode(@PathVariable String roleCode) {
        return ResponseEntity.ok(roleService.getRoleByCode(roleCode));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/hierarchy")   // phân cấp role
    public ResponseEntity<List<RoleResponseDTO>> getRolesHierarchy() {
        return ResponseEntity.ok(roleService.getRolesHierarchy());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}


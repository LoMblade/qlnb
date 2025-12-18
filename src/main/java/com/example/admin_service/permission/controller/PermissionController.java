package com.example.admin_service.permission.controller;

import com.example.admin_service.permission.dto.PermissionRequestDTO;
import com.example.admin_service.permission.dto.PermissionResponseDTO;
import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'PERMISSION:CREATE')")
    public ResponseEntity<PermissionResponseDTO> createPermission(
            @Valid @RequestBody PermissionRequestDTO dto) {
        return ResponseEntity.ok(permissionService.createPermission(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'PERMISSION:READ')")
    public ResponseEntity<PermissionResponseDTO> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping("/code/{permissionCode}")
    @PreAuthorize("hasPermission(null, 'PERMISSION:READ')")
    public ResponseEntity<PermissionResponseDTO> getPermissionByCode(
            @PathVariable String permissionCode) {
        return ResponseEntity.ok(permissionService.getPermissionByCode(permissionCode));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'PERMISSION:READ_ALL')")
    public ResponseEntity<List<PermissionResponseDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/resource/{resourceType}")
    @PreAuthorize("hasPermission(null, 'PERMISSION:READ')")
    public ResponseEntity<List<PermissionResponseDTO>> getPermissionsByResourceType(
            @PathVariable Permission.ResourceType resourceType) {
        return ResponseEntity.ok(permissionService.getPermissionsByResourceType(resourceType));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'PERMISSION:UPDATE')")
    public ResponseEntity<PermissionResponseDTO> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequestDTO dto) {
        return ResponseEntity.ok(permissionService.updatePermission(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'PERMISSION:DELETE')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.admin_service.rolepermission.controller;

import com.example.admin_service.rolepermission.dto.AssignPermissionRequestDTO;
import com.example.admin_service.rolepermission.service.RolePermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final com.example.admin_service.security.JwtTokenProvider jwtTokenProvider;

    @PostMapping("/assign")
    public ResponseEntity<Void> assignPermissionsToRole(
            @Valid @RequestBody AssignPermissionRequestDTO dto,
            HttpServletRequest request) {
        // Only ADMIN can assign permissions
        String token = extractToken(request);
        String roleCode = token != null ? jwtTokenProvider.getRoleFromJWT(token) : null;
        
        if (!"ADMIN".equals(roleCode)) {
            throw new RuntimeException("Only ADMIN can assign permissions");
        }
        
        rolePermissionService.assignPermissionsToRole(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleCode}/{permissionCode}")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable String roleCode,
            @PathVariable String permissionCode,
            HttpServletRequest request) {
        // Only ADMIN can remove permissions
        String token = extractToken(request);
        String currentRoleCode = token != null ? jwtTokenProvider.getRoleFromJWT(token) : null;
        
        if (!"ADMIN".equals(currentRoleCode)) {
            throw new RuntimeException("Only ADMIN can remove permissions");
        }
        
        rolePermissionService.removePermissionFromRole(roleCode, permissionCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleCode}")
    public ResponseEntity<List<String>> getPermissionsByRoleCode(@PathVariable String roleCode) {
        return ResponseEntity.ok(rolePermissionService.getPermissionsByRoleCode(roleCode));
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}


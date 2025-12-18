package com.example.admin_service.rolepermission.controller;

import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.rolepermission.service.RolePermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final com.example.admin_service.security.JwtTokenProvider jwtTokenProvider;

    // Xóa quyền khỏi role (theo resource + action)
    @DeleteMapping("/{roleCode}")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable String roleCode,
            @RequestParam Permission.ResourceType resourceType,
            @RequestParam Permission.ActionType actionType,
            HttpServletRequest request
    ) {
        checkAdmin(request);

        rolePermissionService.removePermissionFromRole(
                roleCode,
                resourceType,
                actionType
        );

        return ResponseEntity.noContent().build();
    }

    // Check quyền ADMIN
    private void checkAdmin(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing token");
        }

        String token = bearer.substring(7);
        String roleCode = jwtTokenProvider.getRoleFromJWT(token);

        if (!"ADMIN".equals(roleCode)) {
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }
    }
}

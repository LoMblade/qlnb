package com.example.admin_service.security;

import com.example.admin_service.rolepermission.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final RolePermissionService rolePermissionService;

    /**
     * Dùng cho:
     * @PreAuthorize("hasPermission(null, 'USER_UPDATE')")
     */
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // ================== ADMIN BYPASS ==================
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }
        // =================================================

        if (!(permission instanceof String)) {
            return false;
        }

        String permissionCode = (String) permission;
        String roleCode = extractRoleCode(authentication);

        if (roleCode == null || permissionCode.isBlank()) {
            return false;
        }

        return rolePermissionService.hasPermission(roleCode, permissionCode);
    }

    /**
     * Dùng cho:
     * @PreAuthorize("hasPermission(#id, 'User', 'UPDATE')")
     */
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission
    ) {
        // Không dùng targetType → chỉ check permissionCode
        return hasPermission(authentication, null, permission);
    }

    /* ===================== HELPER METHODS ===================== */

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private String extractRoleCode(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5)) // bỏ ROLE_
                .findFirst()
                .orElse(null);
    }
}

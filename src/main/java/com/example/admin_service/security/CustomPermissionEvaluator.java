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

    // Service dùng để kiểm tra permission của role
    private final RolePermissionService rolePermissionService;

    // Method này được gọi khi dùng:
    // @PreAuthorize("hasPermission(null, 'USER_UPDATE')")
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission
    ) {

        // Nếu chưa đăng nhập hoặc authentication không hợp lệ → từ chối
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Nếu user có role ADMIN thì cho phép mọi quyền
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }
        // =================================================

        // permission bắt buộc phải là String (permissionCode)
        if (!(permission instanceof String)) {
            return false;
        }

        // Lấy permissionCode (vd: USER_UPDATE)
        String permissionCode = (String) permission;

        // Lấy roleCode từ Authentication (vd: ADMIN, TEAM_LEAD, USER)
        String roleCode = extractRoleCode(authentication);

        // Nếu thiếu role hoặc permission rỗng → từ chối
        if (roleCode == null || permissionCode.isBlank()) {
            return false;
        }

        // Gọi service để kiểm tra role có được cấp permission không
        return rolePermissionService.hasPermission(roleCode, permissionCode);
    }

    // Method này được gọi khi dùng:
    // @PreAuthorize("hasPermission(#id, 'User', 'UPDATE')")
    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission
    ) {
        // Không dùng targetId và targetType
        // Chỉ forward sang method kiểm tra permissionCode
        return hasPermission(authentication, null, permission);
    }

    // Kiểm tra user có role cụ thể hay không (vd: ROLE_ADMIN)
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    // Trích roleCode từ GrantedAuthority
    private String extractRoleCode(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5)) // bỏ tiền tố "ROLE_"
                .findFirst()
                .orElse(null);
    }
}

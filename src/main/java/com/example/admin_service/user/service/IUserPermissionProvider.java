package com.example.admin_service.user.service;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.entity.User;

/**
 * Central RBAC policy definition.
 * Only contains permission rules – NO business logic.
 */
public interface IUserPermissionProvider {

    /* ================= VIEW ================= */

    void canViewUser(CustomUserPrincipal actor, User target);

    void canViewUsers(CustomUserPrincipal actor);

    /* ================= CREATE ================= */

    void canCreateUser(
            CustomUserPrincipal actor,
            String targetRoleCode,
            String targetDepartmentCode
    );

    /* ================= UPDATE ================= */

    void canUpdateUser(CustomUserPrincipal actor, User target);

    /* ================= DELETE ================= */

    void canDeleteUser(CustomUserPrincipal actor, User target);

    /* ================= ASSIGN ROLE ================= */

    void canAssignRole(CustomUserPrincipal actor);
}

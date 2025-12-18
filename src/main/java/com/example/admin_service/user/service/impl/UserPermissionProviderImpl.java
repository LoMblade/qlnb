package com.example.admin_service.user.service.impl;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.service.IUserPermissionProvider;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionProviderImpl implements IUserPermissionProvider {

    @Override
    public void canViewUser(CustomUserPrincipal actor, User target) {

        // ADMIN: view all
        if (isAdmin(actor)) return;

        // TEAM_LEAD: only USER in same department
        if (isTeamLead(actor)) {
            if (!sameDepartment(actor, target)) {
                throw new RuntimeException("TEAM_LEAD chỉ được xem user trong phòng ban");
            }
            if (isAdminOrTeamLead(target)) {
                throw new RuntimeException("TEAM_LEAD không được xem ADMIN / TEAM_LEAD");
            }
            return;
        }

        // USER: only self
        if (actor.getUsername().equals(target.getUsername())) return;

        throw new RuntimeException("Không có quyền xem user");
    }

    @Override
    public void canViewUsers(CustomUserPrincipal actor) {
        if (isAdmin(actor) || isTeamLead(actor)) return;
        throw new RuntimeException("USER không có quyền xem danh sách user");
    }

    @Override
    public void canCreateUser(
            CustomUserPrincipal actor,
            String targetRoleCode,
            String targetDepartmentCode
    ) {
        if (isAdmin(actor)) return;

        if (isTeamLead(actor)) {
            if ("ADMIN".equals(targetRoleCode) || "TEAM_LEAD".equals(targetRoleCode)) {
                throw new RuntimeException("TEAM_LEAD không thể tạo ADMIN / TEAM_LEAD");
            }
            // TEAM_LEAD must have a department and may only create users in their own department
            if (actor.getDepartmentCode() == null) {
                throw new RuntimeException("TEAM_LEAD chưa có phòng ban, không thể tạo user");
            }
            if (targetDepartmentCode == null || !actor.getDepartmentCode().equals(targetDepartmentCode)) {
                throw new RuntimeException("TEAM_LEAD chỉ được tạo user trong phòng ban của mình");
            }
            return;
        }

        throw new RuntimeException("USER không có quyền tạo user");
    }

    @Override
    public void canUpdateUser(CustomUserPrincipal actor, User target) {

        if (isAdmin(actor)) return;

        if (isTeamLead(actor)) {
            if (!sameDepartment(actor, target)) {
                throw new RuntimeException("TEAM_LEAD chỉ cập nhật user trong phòng ban");
            }
            if (isAdminOrTeamLead(target)) {
                throw new RuntimeException("TEAM_LEAD không cập nhật ADMIN / TEAM_LEAD");
            }
            return;
        }

        if (actor.getUsername().equals(target.getUsername())) return;

        throw new RuntimeException("Không có quyền cập nhật user");
    }

    @Override
    public void canDeleteUser(CustomUserPrincipal actor, User target) {

        if (isAdmin(actor)) return;

        if (isTeamLead(actor)) {
            if (!sameDepartment(actor, target)) {
                throw new RuntimeException("TEAM_LEAD chỉ xóa user trong phòng ban");
            }
            if (isAdminOrTeamLead(target)) {
                throw new RuntimeException("TEAM_LEAD không xóa ADMIN / TEAM_LEAD");
            }
            return;
        }

        throw new RuntimeException("Không có quyền xóa user");
    }

    @Override
    public void canAssignRole(CustomUserPrincipal actor) {
        if (!isAdmin(actor)) {
            throw new RuntimeException("Chỉ ADMIN mới được gán role");
        }
    }

    // ==================== PRIVATE ====================

    private boolean isAdmin(CustomUserPrincipal u) {
        return "ADMIN".equals(u.getRoleCode());
    }

    private boolean isTeamLead(CustomUserPrincipal u) {
        return "TEAM_LEAD".equals(u.getRoleCode());
    }

    private boolean isAdminOrTeamLead(User u) {
        return u.getRole() != null &&
                ("ADMIN".equals(u.getRole().getRoleCode())
                        || "TEAM_LEAD".equals(u.getRole().getRoleCode()));
    }

    private boolean sameDepartment(CustomUserPrincipal actor, User target) {
        return actor.getDepartmentCode() != null
                && target.getDepartment() != null
                && actor.getDepartmentCode()
                .equals(target.getDepartment().getDepartmentCode());
    }
}

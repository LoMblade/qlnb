package com.example.admin_service.config;

import com.example.admin_service.department.entity.Department;
import com.example.admin_service.department.repository.DepartmentRepository;
import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.permission.repository.PermissionRepository;
import com.example.admin_service.role.entity.Role;
import com.example.admin_service.role.repository.RoleRepository;
import com.example.admin_service.rolepermission.entity.RolePermission;
import com.example.admin_service.rolepermission.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final DepartmentRepository departmentRepository;


    @Override
    public void run(String... args) {
        initializeRoles();       // Khởi tạo các vai trò mặc định
        initializePermissions(); // Khởi tạo quyền mặc định
        assignAdminPermissions();// Gán tất cả quyền cho ADMIN
        log.info("khởi tạo data xong");
    }

// khởi tạo role mặc định
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            // ADMIN role: root role
            Role adminRole = Role.builder()
                    .roleCode("ADMIN")
                    .name("Administrator")
                    .description("Full system administrator with all permissions")
                    .parent(null)
                    .build();
            adminRole = roleRepository.save(adminRole);

            // TEAM_LEAD role: child của ADMIN
            Role teamLeadRole = Role.builder()
                    .roleCode("TEAM_LEAD")
                    .name("Team Leader")
                    .description("Team leader with department management permissions")
                    .parent(adminRole)
                    .build();
            teamLeadRole = roleRepository.save(teamLeadRole);

            // USER role: child của TEAM_LEAD
            Role userRole = Role.builder()
                    .roleCode("USER")
                    .name("User")
                    .description("Regular user with limited permissions")
                    .parent(teamLeadRole)
                    .build();
            roleRepository.save(userRole);

            log.info("Roles initialized: ADMIN, TEAM_LEAD, USER");
        }
    }

    // tạo permission mặc dịnh
    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            // Department permissions
            createPermission("DEPT_CREATE", "Create Department", "Create new department", Permission.ResourceType.DEPARTMENT, Permission.ActionType.CREATE);
            createPermission("DEPT_READ", "Read Department", "View department information", Permission.ResourceType.DEPARTMENT, Permission.ActionType.READ);
            createPermission("DEPT_UPDATE", "Update Department", "Update department information", Permission.ResourceType.DEPARTMENT, Permission.ActionType.UPDATE);
            createPermission("DEPT_DELETE", "Delete Department", "Delete department", Permission.ResourceType.DEPARTMENT, Permission.ActionType.DELETE);

            // User permissions
            createPermission("USER_CREATE", "Create User", "Create new user", Permission.ResourceType.USER, Permission.ActionType.CREATE);
            createPermission("USER_READ", "Read User", "View user information", Permission.ResourceType.USER, Permission.ActionType.READ);
            createPermission("USER_UPDATE", "Update User", "Update user information", Permission.ResourceType.USER, Permission.ActionType.UPDATE);
            createPermission("USER_DELETE", "Delete User", "Delete user", Permission.ResourceType.USER, Permission.ActionType.DELETE);

            // Team Lead permissions
            createPermission("TEAM_LEAD_CREATE", "Create Team Lead", "Create new team lead", Permission.ResourceType.TEAM_LEAD, Permission.ActionType.CREATE);
            createPermission("TEAM_LEAD_READ", "Read Team Lead", "View team lead information", Permission.ResourceType.TEAM_LEAD, Permission.ActionType.READ);
            createPermission("TEAM_LEAD_UPDATE", "Update Team Lead", "Update team lead information", Permission.ResourceType.TEAM_LEAD, Permission.ActionType.UPDATE);
            createPermission("TEAM_LEAD_DELETE", "Delete Team Lead", "Delete team lead", Permission.ResourceType.TEAM_LEAD, Permission.ActionType.DELETE);

            // Role permissions
            createPermission("ROLE_CREATE", "Create Role", "Create new role", Permission.ResourceType.ROLE, Permission.ActionType.CREATE);
            createPermission("ROLE_READ", "Read Role", "View role information", Permission.ResourceType.ROLE, Permission.ActionType.READ);
            createPermission("ROLE_UPDATE", "Update Role", "Update role information", Permission.ResourceType.ROLE, Permission.ActionType.UPDATE);
            createPermission("ROLE_DELETE", "Delete Role", "Delete role", Permission.ResourceType.ROLE, Permission.ActionType.DELETE);

            // Permission management permissions
            createPermission("PERM_CREATE", "Create Permission", "Create new permission", Permission.ResourceType.PERMISSION, Permission.ActionType.CREATE);
            createPermission("PERM_READ", "Read Permission", "View permission information", Permission.ResourceType.PERMISSION, Permission.ActionType.READ);
            createPermission("PERM_UPDATE", "Update Permission", "Update permission information", Permission.ResourceType.PERMISSION, Permission.ActionType.UPDATE);
            createPermission("PERM_DELETE", "Delete Permission", "Delete permission", Permission.ResourceType.PERMISSION, Permission.ActionType.DELETE);

            log.info("Permissions đã khởi tạo");
        }
    }

    /**
     * Tạo một permission nếu chưa tồn tại
     */
    private void createPermission(String code, String name, String description, Permission.ResourceType resourceType, Permission.ActionType actionType) {
        if (!permissionRepository.existsByPermissionCode(code)) {
            Permission permission = Permission.builder()
                    .permissionCode(code)
                    .name(name)
                    .description(description)
                    .resourceType(resourceType)
                    .actionType(actionType)
                    .build();
            permissionRepository.save(permission);
        }
    }

    /**
     * Gán tất cả permission hiện tại cho ADMIN role
     */
    private void assignAdminPermissions() {
        Role adminRole = roleRepository.findByRoleCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        // Chỉ gán nếu chưa có permission nào
        if (rolePermissionRepository.findByRoleId(adminRole.getId()).isEmpty()) {
            List<Permission> allPermissions = permissionRepository.findAll();
            for (Permission permission : allPermissions) {
                RolePermission rolePermission = RolePermission.builder()
                        .role(adminRole)
                        .permission(permission)
                        .build();
                rolePermissionRepository.save(rolePermission);
            }
            log.info("All permissions assigned to ADMIN role");
        }
    }
}

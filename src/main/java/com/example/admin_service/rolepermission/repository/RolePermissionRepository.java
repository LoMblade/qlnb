package com.example.admin_service.rolepermission.repository;

import com.example.admin_service.permission.entity.Permission;
import com.example.admin_service.role.entity.Role;
import com.example.admin_service.rolepermission.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
// select tất cả permission của 1 role
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.roleCode = :roleCode")
    List<RolePermission> findByRoleCode(@Param("roleCode") String roleCode);
// tìm permission của 1 role theo ID
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<RolePermission> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT rp FROM RolePermission rp WHERE rp.permission.permissionCode = :permissionCode")
    List<RolePermission> findByPermissionCode(@Param("permissionCode") String permissionCode);
// kiểm tra role có quyền này chưa ?
    @Query("""
           SELECT rp FROM RolePermission rp
           WHERE rp.role.roleCode = :roleCode
             AND rp.permission.permissionCode = :permissionCode
           """)
    Optional<RolePermission> findByRoleCodeAndPermissionCode(
            @Param("roleCode") String roleCode,
            @Param("permissionCode") String permissionCode
    );

    // Xóa toàn bộ permission của role
    void deleteByRoleId(Long roleId);

    Optional<RolePermission> findByRoleAndPermission(
            Role role,
            Permission permission
    );
}

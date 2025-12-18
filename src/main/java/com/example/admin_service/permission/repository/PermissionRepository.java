package com.example.admin_service.permission.repository;

import com.example.admin_service.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    @Query("SELECT p FROM Permission p WHERE p.resourceType = :resourceType")
    List<Permission> findByResourceType(
            @Param("resourceType") Permission.ResourceType resourceType
    );

    @Query("""
        SELECT p.permissionCode
        FROM com.example.admin_service.rolepermission.entity.RolePermission rp
        JOIN rp.permission p
        WHERE rp.role.roleCode = :roleCode
    """)
    List<String> findPermissionCodesByRoleCode(
            @Param("roleCode") String roleCode
    );
}



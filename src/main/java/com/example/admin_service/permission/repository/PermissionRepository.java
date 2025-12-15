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
    List<Permission> findByResourceType(@Param("resourceType") Permission.ResourceType resourceType);
    
    @Query("SELECT p FROM Permission p WHERE p.resourceType = :resourceType AND p.actionType = :actionType")
    List<Permission> findByResourceTypeAndActionType(
        @Param("resourceType") Permission.ResourceType resourceType,
        @Param("actionType") Permission.ActionType actionType
    );
}


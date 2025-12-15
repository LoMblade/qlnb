package com.example.admin_service.role.repository;

import com.example.admin_service.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);
    boolean existsByRoleCode(String roleCode);
    
    @Query("SELECT r FROM Role r WHERE r.parent IS NULL")
    List<Role> findRootRoles();
    
    @Query("SELECT r FROM Role r WHERE r.parent.id = :parentId")
    List<Role> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT r FROM Role r WHERE r.parent.roleCode = :parentRoleCode")
    List<Role> findByParentRoleCode(@Param("parentRoleCode") String parentRoleCode);
}


package com.example.admin_service.user.repository;

import com.example.admin_service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /* ================= BASIC ================= */

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.department IS NOT NULL
          AND u.department.departmentCode = :departmentCode
    """)
    List<User> findByDepartmentCode(@Param("departmentCode") String departmentCode);

// lấy user theo role
    @Query("""
        SELECT u
        FROM User u
        WHERE u.role IS NOT NULL
          AND u.role.roleCode = :roleCode
    """)
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

// Lấy user theo department + role
    @Query("""
        SELECT u
        FROM User u
        WHERE u.department IS NOT NULL
          AND u.role IS NOT NULL
          AND u.department.departmentCode = :departmentCode
          AND u.role.roleCode = :roleCode
    """)
    List<User> findByDepartmentCodeAndRoleCode(
            @Param("departmentCode") String departmentCode,
            @Param("roleCode") String roleCode
    );

// TEAM_LEAD duy nhất của một department
    @Query("""
        SELECT u
        FROM User u
        WHERE u.department IS NOT NULL
          AND u.role IS NOT NULL
          AND u.department.departmentCode = :departmentCode
          AND u.role.roleCode = 'TEAM_LEAD'
    """)
    Optional<User> findTeamLeadByDepartmentCode(
            @Param("departmentCode") String departmentCode
    );

//User chưa được gán department
    @Query("""
        SELECT u
        FROM User u
        WHERE u.department IS NULL
    """)
    List<User> findUsersWithoutDepartment();
}

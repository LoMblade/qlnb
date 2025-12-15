package com.example.admin_service.user.controller;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.dto.*;
import com.example.admin_service.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* =========================
       HELPER METHODS
       ========================= */

    private CustomUserPrincipal principal() {
        return (CustomUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getCurrentUsername() {
        return principal().getUsername();
    }

    private String getCurrentRoleCode() {
        return principal().getRoleCode();
    }

    private String getCurrentDepartmentCode() {
        return principal().getDepartmentCode();
    }

    /* =========================
       API METHODS
       ========================= */

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(
                userService.createUser(
                        dto,
                        getCurrentRoleCode(),
                        getCurrentDepartmentCode()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        String roleCode = getCurrentRoleCode();
        String departmentCode = getCurrentDepartmentCode();

        if ("ADMIN".equals(roleCode)) {
            return ResponseEntity.ok(userService.getAllUsers());
        }

        if ("TEAM_LEAD".equals(roleCode) && departmentCode != null) {
            return ResponseEntity.ok(
                    userService.getUsersInMyDepartment(departmentCode)
            );
        }

        return ResponseEntity.ok(List.of());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(
                userService.updateUser(
                        id,
                        dto,
                        getCurrentRoleCode(),
                        getCurrentDepartmentCode()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(
                id,
                getCurrentRoleCode(),
                getCurrentDepartmentCode()
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/assign-role")
    public ResponseEntity<UserResponseDTO> assignRoleAndDepartment(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequestDTO dto) {

        if (!"ADMIN".equals(getCurrentRoleCode())) {
            throw new RuntimeException("Only ADMIN can assign role and department");
        }

        return ResponseEntity.ok(
                userService.assignRoleAndDepartment(
                        id,
                        dto.getRoleCode(),
                        dto.getDepartmentCode()
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        return ResponseEntity.ok(
                userService.getMyProfile(getCurrentUsername())
        );
    }

    @GetMapping("/without-department")
    public ResponseEntity<List<UserResponseDTO>> getUsersWithoutDepartment() {

        String roleCode = getCurrentRoleCode();
        if (!"ADMIN".equals(roleCode) && !"TEAM_LEAD".equals(roleCode)) {
            throw new RuntimeException("You don't have permission to view users without department");
        }

        return ResponseEntity.ok(
                userService.getUsersWithoutDepartment()
        );
    }

    @PostMapping("/add-to-department")
    public ResponseEntity<UserResponseDTO> addUserToMyDepartment(
            @Valid @RequestBody AddUserToDepartmentRequestDTO dto) {

        if (!"TEAM_LEAD".equals(getCurrentRoleCode())) {
            throw new RuntimeException("Only TEAM_LEAD can add users to department");
        }

        return ResponseEntity.ok(
                userService.addUserToMyDepartment(
                        dto.getUsername(),
                        getCurrentDepartmentCode()
                )
        );
    }
}


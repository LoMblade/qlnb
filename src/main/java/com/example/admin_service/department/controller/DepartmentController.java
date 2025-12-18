package com.example.admin_service.department.controller;

import com.example.admin_service.department.dto.DepartmentRequestDTO;
import com.example.admin_service.department.dto.DepartmentResponseDTO;
import com.example.admin_service.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * ADMIN tạo phòng ban
     */
    @PreAuthorize("hasPermission(null, 'DEPARTMENT:CREATE')")
    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(
            @Valid @RequestBody DepartmentRequestDTO dto) {
        return ResponseEntity.ok(departmentService.createDepartment(dto));
    }

    /**
     * ADMIN, TEAM_LEAD xem phòng ban
     */
    @PreAuthorize("hasPermission(null, 'DEPARTMENT:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PreAuthorize("hasPermission(null, 'DEPARTMENT:READ')")
    @GetMapping("/code/{departmentCode}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByCode(
            @PathVariable String departmentCode) {
        return ResponseEntity.ok(departmentService.getDepartmentByCode(departmentCode));
    }

    /**
     * Chỉ ADMIN được xem toàn bộ departments
     */
    @PreAuthorize("hasPermission(null, 'DEPARTMENT:READ_ALL')")
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    /**
     * ADMIN update phòng ban
     */
    @PreAuthorize("hasPermission(null, 'DEPARTMENT:UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDTO dto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }

    /**
     * ADMIN delete phòng ban
     */
    @PreAuthorize("hasPermission(null, 'DEPARTMENT:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}

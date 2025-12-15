package com.example.admin_service.department.service;

import com.example.admin_service.department.dto.DepartmentRequestDTO;
import com.example.admin_service.department.dto.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto);
    DepartmentResponseDTO getDepartmentById(Long id);
    DepartmentResponseDTO getDepartmentByCode(String departmentCode);
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto);
    void deleteDepartment(Long id);
}


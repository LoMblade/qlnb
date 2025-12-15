package com.example.admin_service.department.service;

import com.example.admin_service.department.dto.DepartmentRequestDTO;
import com.example.admin_service.department.dto.DepartmentResponseDTO;
import com.example.admin_service.department.entity.Department;
import com.example.admin_service.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {
        if (departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new RuntimeException("Department code already exists: " + dto.getDepartmentCode());
        }

        Department department = Department.builder()
                .departmentCode(dto.getDepartmentCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        department = departmentRepository.save(department);
        return mapToDTO(department);
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return mapToDTO(department);
    }

    @Override
    public DepartmentResponseDTO getDepartmentByCode(String departmentCode) {
        Department department = departmentRepository.findByDepartmentCode(departmentCode)
                .orElseThrow(() -> new RuntimeException("Department not found with code: " + departmentCode));
        return mapToDTO(department);
    }

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        if (!department.getDepartmentCode().equals(dto.getDepartmentCode()) &&
            departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new RuntimeException("Department code already exists: " + dto.getDepartmentCode());
        }

        department.setDepartmentCode(dto.getDepartmentCode());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());

        department = departmentRepository.save(department);
        return mapToDTO(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentResponseDTO mapToDTO(Department department) {
        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }
}


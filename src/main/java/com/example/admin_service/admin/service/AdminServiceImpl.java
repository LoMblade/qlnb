package com.example.admin_service.admin.service;

import com.example.admin_service.admin.dto.AdminDTO;
import com.example.admin_service.admin.dto.AdminResponseDTO;
import com.example.admin_service.admin.entity.Admin;
import com.example.admin_service.admin.repository.AdminRepository;
import com.example.admin_service.admin.role.Role;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDTO createAdmin(AdminDTO dto) {

        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(Role.ADMIN)   // mặc định ADMIN
                .build();

        Admin saved = adminRepository.save(admin);

        return AdminResponseDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }

    @Override
    public AdminResponseDTO getAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return AdminResponseDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .build();
    }
}

package com.example.admin_service.admin.service;

import com.example.admin_service.admin.dto.AdminDTO;
import com.example.admin_service.admin.dto.AdminResponseDTO;

public interface AdminService {
    AdminResponseDTO createAdmin(AdminDTO adminDTO);

    AdminResponseDTO getAdmin(Long id);
}

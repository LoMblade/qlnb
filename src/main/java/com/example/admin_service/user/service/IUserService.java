package com.example.admin_service.user.service;

import com.example.admin_service.user.dto.CreateUserRequestDTO;
import com.example.admin_service.user.dto.UpdateUserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;

import java.util.List;

public interface IUserService {

    // ==================== CURRENT USER ====================
    UserResponseDTO getMyProfile();

    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getUsers();

    UserResponseDTO createUser(CreateUserRequestDTO dto);

    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto);

    void deleteUser(Long id);

    UserResponseDTO assignRoleAndDepartment(
            Long userId,
            String roleCode,
            String departmentCode
    );
}

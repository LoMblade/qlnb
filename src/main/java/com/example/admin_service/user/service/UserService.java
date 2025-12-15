package com.example.admin_service.user.service;

import com.example.admin_service.user.dto.UserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto, String currentUserRoleCode, String currentUserDepartmentCode);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    List<UserResponseDTO> getAllUsers();
    List<UserResponseDTO> getUsersByDepartment(String departmentCode);
    List<UserResponseDTO> getUsersByRole(String roleCode);
    UserResponseDTO updateUser(Long id, UserRequestDTO dto, String currentUserRoleCode, String currentUserDepartmentCode);
    void deleteUser(Long id, String currentUserRoleCode, String currentUserDepartmentCode);
    List<UserResponseDTO> getUsersInMyDepartment(String currentUserDepartmentCode);
    UserResponseDTO assignRoleAndDepartment(Long userId, String roleCode, String departmentCode);
    UserResponseDTO getMyProfile(String username);
    List<UserResponseDTO> getUsersWithoutDepartment();
    UserResponseDTO addUserToMyDepartment(String username, String currentUserDepartmentCode);
}


package com.example.admin_service.user.service;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.dto.CreateUserRequestDTO;
import com.example.admin_service.user.dto.UpdateUserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;
import com.example.admin_service.user.entity.User;

import java.util.List;

public interface IUserHelper {

    User findUserById(Long id);

    User findUserByUsername(String username);

    UserResponseDTO mapToDTO(User user);

    java.util.List<UserResponseDTO> getUsersVisibleFor(com.example.admin_service.security.CustomUserPrincipal principal);


    List<UserResponseDTO> getUsersWithoutDepartment();

    UserResponseDTO createUserEntity(CreateUserRequestDTO dto);

    UserResponseDTO updateUserEntity(Long id, UpdateUserRequestDTO dto);

    void deleteUser(Long id);

//    UserResponseDTO addUserToMyDepartment(String username, String deptCode);
}

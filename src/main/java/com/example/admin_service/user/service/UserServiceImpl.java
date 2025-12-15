package com.example.admin_service.user.service;

import com.example.admin_service.department.entity.Department;
import com.example.admin_service.department.repository.DepartmentRepository;
import com.example.admin_service.role.entity.Role;
import com.example.admin_service.role.repository.RoleRepository;
import com.example.admin_service.user.dto.UserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;
import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto, String currentUserRoleCode, String currentUserDepartmentCode) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        Role role = null;
        Department department = null;

        // Chỉ ADMIN mới có thể tạo user với role và department
        // TEAM_LEAD chỉ có thể thêm user không có department vào phòng ban của mình
        if ("ADMIN".equals(currentUserRoleCode)) {
            if (dto.getRoleCode() != null && !dto.getRoleCode().isEmpty()) {
                role = roleRepository.findByRoleCode(dto.getRoleCode())
                        .orElseThrow(() -> new RuntimeException("Role not found with code: " + dto.getRoleCode()));
            }
            if (dto.getDepartmentCode() != null && !dto.getDepartmentCode().isEmpty()) {
                department = departmentRepository.findByDepartmentCode(dto.getDepartmentCode())
                        .orElseThrow(() -> new RuntimeException("Department not found with code: " + dto.getDepartmentCode()));
            }
        } else if ("TEAM_LEAD".equals(currentUserRoleCode)) {
            // TEAM_LEAD chỉ có thể thêm user không có department vào phòng ban của mình
            // User được tạo sẽ không có role (null), chỉ có department của TEAM_LEAD
            // TEAM_LEAD không thể tạo user với role, user sẽ không có role
            role = null;
            
            // TEAM_LEAD chỉ có thể thêm user vào phòng ban của mình
            if (dto.getDepartmentCode() != null && !dto.getDepartmentCode().isEmpty()) {
                if (!dto.getDepartmentCode().equals(currentUserDepartmentCode)) {
                    throw new RuntimeException("You can only add users to your own department");
                }
            }
            
            // Gán department của TEAM_LEAD cho user mới
            department = departmentRepository.findByDepartmentCode(currentUserDepartmentCode)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        } else {
            // User thường không thể tạo user
            throw new RuntimeException("You don't have permission to create users");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(role)
                .department(department)
                .active(true)
                .build();

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getUsersByDepartment(String departmentCode) {
        return userRepository.findByDepartmentCode(departmentCode).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(String roleCode) {
        return userRepository.findByRoleCode(roleCode).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto, String currentUserRoleCode, String currentUserDepartmentCode) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check permissions based on role
        if (!"ADMIN".equals(currentUserRoleCode)) {
            if ("TEAM_LEAD".equals(currentUserRoleCode)) {
                // Team lead can only update users in their department
                if (user.getDepartment() == null || 
                    !user.getDepartment().getDepartmentCode().equals(currentUserDepartmentCode)) {
                    throw new RuntimeException("You can only update users in your department");
                }
            } else {
                // Regular users cannot update other users
                throw new RuntimeException("You don't have permission to update users");
            }
        }

        if (!user.getUsername().equals(dto.getUsername()) && 
            userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }

        if (dto.getEmail() != null && !user.getEmail().equals(dto.getEmail()) &&
            userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        // TEAM_LEAD không thể update role và department của user
        // Chỉ ADMIN mới có thể update role và department
        Role role = user.getRole();
        Department department = user.getDepartment();
        
        if ("ADMIN".equals(currentUserRoleCode)) {
            // Admin có thể update role và department
            if (dto.getRoleCode() != null && !dto.getRoleCode().isEmpty()) {
                role = roleRepository.findByRoleCode(dto.getRoleCode())
                        .orElseThrow(() -> new RuntimeException("Role not found with code: " + dto.getRoleCode()));
            }
            if (dto.getDepartmentCode() != null && !dto.getDepartmentCode().isEmpty()) {
                department = departmentRepository.findByDepartmentCode(dto.getDepartmentCode())
                        .orElseThrow(() -> new RuntimeException("Department not found with code: " + dto.getDepartmentCode()));
            }
        } else if ("TEAM_LEAD".equals(currentUserRoleCode)) {
            // TEAM_LEAD không thể update role và department
            // Giữ nguyên role và department hiện tại
        }

        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setEmail(dto.getEmail());
        user.setRole(role);
        user.setDepartment(department);

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentUserRoleCode, String currentUserDepartmentCode) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check permissions based on role
        if (!"ADMIN".equals(currentUserRoleCode)) {
            if ("TEAM_LEAD".equals(currentUserRoleCode)) {
                // Team lead can only delete users in their department
                if (user.getDepartment() == null || 
                    !user.getDepartment().getDepartmentCode().equals(currentUserDepartmentCode)) {
                    throw new RuntimeException("You can only delete users in your department");
                }
            } else {
                // Regular users cannot delete users
                throw new RuntimeException("You don't have permission to delete users");
            }
        }

        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> getUsersInMyDepartment(String currentUserDepartmentCode) {
        return userRepository.findByDepartmentCode(currentUserDepartmentCode).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public List<UserResponseDTO> getUsersWithoutDepartment() {
        return userRepository.findUsersWithoutDepartment().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO addUserToMyDepartment(String username, String currentUserDepartmentCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Kiểm tra user không có department
        if (user.getDepartment() != null) {
            throw new RuntimeException("User already belongs to a department");
        }

        // Gán department cho user
        Department department = departmentRepository.findByDepartmentCode(currentUserDepartmentCode)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        user.setDepartment(department);
        // User vẫn không có role (null)

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO assignRoleAndDepartment(Long userId, String roleCode, String departmentCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Role role = null;
        if (roleCode != null && !roleCode.isEmpty()) {
            role = roleRepository.findByRoleCode(roleCode)
                    .orElseThrow(() -> new RuntimeException("Role not found with code: " + roleCode));
        }

        Department department = null;
        if (departmentCode != null && !departmentCode.isEmpty()) {
            department = departmentRepository.findByDepartmentCode(departmentCode)
                    .orElseThrow(() -> new RuntimeException("Department not found with code: " + departmentCode));
        }

        // Kiểm tra: 1 department chỉ có 1 TEAM_LEAD
        if ("TEAM_LEAD".equals(roleCode) && department != null) {
            Optional<User> existingTeamLead = userRepository.findTeamLeadByDepartmentCode(departmentCode);
            if (existingTeamLead.isPresent() && !existingTeamLead.get().getId().equals(userId)) {
                throw new RuntimeException("Department " + departmentCode + " already has a TEAM_LEAD");
            }
        }

        user.setRole(role);
        user.setDepartment(department);

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleCode(user.getRole() != null ? user.getRole().getRoleCode() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .departmentCode(user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .active(user.getActive())
                .build();
    }
}


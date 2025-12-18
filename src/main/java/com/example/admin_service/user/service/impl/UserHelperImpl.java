package com.example.admin_service.user.service.impl;

import com.example.admin_service.department.repository.DepartmentRepository;
import com.example.admin_service.role.repository.RoleRepository;
import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.dto.CreateUserRequestDTO;
import com.example.admin_service.user.dto.UpdateUserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;
import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import com.example.admin_service.user.service.IUserHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserHelperImpl implements IUserHelper {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserHelperImpl(UserRepository userRepository,
                          RoleRepository roleRepository,
                          DepartmentRepository departmentRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Tìm user theo ID, không có thì throw lỗi
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Tìm user theo username
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Map User entity → UserResponseDTO (trả về cho client)
    @Override
    public UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())

                // Role có thể null → check để tránh NPE
                .roleCode(user.getRole() != null ? user.getRole().getRoleCode() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)

                // Department có thể null
                .departmentCode(user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)

                .active(user.getActive())
                .build();
    }

    // Lấy danh sách user mà người đăng nhập được phép nhìn thấy
    @Override
    public List<UserResponseDTO> getUsersVisibleFor(CustomUserPrincipal principal) {

        // Chưa login
        if (principal == null) return List.of();

        String role = principal.getRoleCode();

        // ADMIN thấy tất cả user
        if ("ADMIN".equals(role)) {
            return userRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        // TEAM_LEAD chỉ thấy user trong phòng ban của mình
        if ("TEAM_LEAD".equals(role)) {
            String dept = principal.getDepartmentCode();
            return userRepository.findByDepartmentCode(dept)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        // USER thường chỉ thấy chính mình
        User me = userRepository.findByUsername(principal.getUsername()).orElse(null);
        if (me == null) return List.of();
        return List.of(mapToDTO(me));
    }

    // Lấy danh sách user chưa thuộc phòng ban nào
    @Override
    public List<UserResponseDTO> getUsersWithoutDepartment() {
        return userRepository.findUsersWithoutDepartment()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Tạo user mới
    @Override
    public UserResponseDTO createUserEntity(CreateUserRequestDTO dto) {

        // Check trùng username
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check trùng email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Tạo user mới
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // mã hóa password
                .active(true)
                .build();

        // Gán role nếu có
        if (dto.getRoleCode() != null) {
            user.setRole(
                    roleRepository.findByRoleCode(dto.getRoleCode()).orElse(null)
            );
        }

        // Gán department nếu có
        if (dto.getDepartmentCode() != null) {
            user.setDepartment(
                    departmentRepository.findByDepartmentCode(dto.getDepartmentCode()).orElse(null)
            );
        }

        // Rule nghiệp vụ:
        // Mỗi phòng ban chỉ có TỐI ĐA 1 TEAM_LEAD
        if (user.getRole() != null
                && "TEAM_LEAD".equals(user.getRole().getRoleCode())
                && user.getDepartment() != null) {
            ensureSingleTeamLead(user.getDepartment().getDepartmentCode(), null);
        }

        return mapToDTO(userRepository.save(user));
    }

    // Cập nhật user
    @Override
    public UserResponseDTO updateUserEntity(Long id, UpdateUserRequestDTO dto) {

        User user = findUserById(id);

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        // Update password nếu có
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoleCode() != null) {
            user.setRole(roleRepository.findByRoleCode(dto.getRoleCode()).orElse(null));
        }

        if (dto.getDepartmentCode() != null) {
            user.setDepartment(
                    departmentRepository.findByDepartmentCode(dto.getDepartmentCode()).orElse(null)
            );
        }

        // Xác định role & department SAU KHI update
        String resultingRole =
                dto.getRoleCode() != null
                        ? dto.getRoleCode()
                        : (user.getRole() != null ? user.getRole().getRoleCode() : null);

        String resultingDept =
                dto.getDepartmentCode() != null
                        ? dto.getDepartmentCode()
                        : (user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null);

        // Enforce rule TEAM_LEAD duy nhất
        if ("TEAM_LEAD".equals(resultingRole) && resultingDept != null) {
            ensureSingleTeamLead(resultingDept, id);
        }

        if (dto.getActive() != null) user.setActive(dto.getActive());

        return mapToDTO(userRepository.save(user));
    }

    // Xóa user
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /* ================= BUSINESS ================= */

    // Thêm user vào phòng ban của mình
    @Override
    public UserResponseDTO addUserToMyDepartment(String username, String deptCode) {

        User user = findUserByUsername(username);

        // Nếu user là TEAM_LEAD → check trùng
        if (user.getRole() != null && "TEAM_LEAD".equals(user.getRole().getRoleCode())) {
            ensureSingleTeamLead(deptCode, user.getId());
        }

        user.setDepartment(
                departmentRepository.findByDepartmentCode(deptCode)
                        .orElseThrow(() -> new RuntimeException("Department not found"))
        );

        return mapToDTO(userRepository.save(user));
    }

    // Đảm bảo mỗi phòng ban chỉ có 1 TEAM_LEAD
    private void ensureSingleTeamLead(String deptCode, Long excludingUserId) {

        userRepository.findTeamLeadByDepartmentCode(deptCode)
                .ifPresent(existing -> {

                    // Nếu có TEAM_LEAD khác → throw lỗi
                    if (excludingUserId == null
                            || !existing.getId().equals(excludingUserId)) {
                        throw new RuntimeException("Phòng ban đã có TEAM_LEAD khác");
                    }
                });
    }
}


package com.example.admin_service.user.service.impl;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.dto.CreateUserRequestDTO;
import com.example.admin_service.user.dto.UpdateUserRequestDTO;
import com.example.admin_service.user.dto.UserResponseDTO;
import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.service.IUserHelper;
import com.example.admin_service.user.service.IUserPermissionProvider;
import com.example.admin_service.user.service.IUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// Đánh dấu đây là một Service của Spring, sẽ được tự động inject khi cần
@Service
public class UserServiceImpl implements IUserService {

    // Các dependency được inject qua constructor (constructor injection)
    private final IUserHelper helper;
    private final IUserPermissionProvider permissionProvider;

    // Constructor để Spring inject các dependency
    public UserServiceImpl(
            IUserHelper helper,
            IUserPermissionProvider permissionProvider
    ) {
        this.helper = helper;
        this.permissionProvider = permissionProvider;
    }

    // Lấy thông tin người dùng hiện tại đang đăng nhập (từ Spring Security)
    private CustomUserPrincipal currentUser() {
        return (CustomUserPrincipal)
                SecurityContextHolder.getContext()     // Lấy context bảo mật
                        .getAuthentication()           // Lấy đối tượng Authentication
                        .getPrincipal();              // Lấy Principal (ở đây là CustomUserPrincipal)
    }

    // Lấy thông tin profile
    @Override
    public UserResponseDTO getMyProfile() {
        return helper.mapToDTO(                              // Chuyển entity sang DTO để trả về
                helper.findUserByUsername(currentUser().getUsername()) // Tìm user theo username hiện tại
        );
    }

    // Lấy thông tin một user theo ID
    @Override
    public UserResponseDTO getUserById(Long id) {
        User target = helper.findUserById(id);               // Tìm user cần xem

        // Kiểm tra quyền: người hiện tại có được phép xem user này không?
        permissionProvider.canViewUser(
                currentUser(),
                target
        );

        return helper.mapToDTO(target);                      // Trả về DTO
    }

    // Lấy danh sách tất cả user mà người hiện tại được phép xem
    @Override
    public List<UserResponseDTO> getUsers() {
        // Kiểm tra quyền xem danh sách user
        permissionProvider.canViewUsers(currentUser());

        // Lấy danh sách user theo quyền (có thể lọc theo department, role...)
        return helper.getUsersVisibleFor(currentUser());
    }

    // Tạo user mới
    @Override
    public UserResponseDTO createUser(CreateUserRequestDTO dto) {
        // Kiểm tra quyền: người hiện tại có được phép tạo user với role + department này không?
        permissionProvider.canCreateUser(
                currentUser(),
                dto.getRoleCode(),
                dto.getDepartmentCode()
        );

        // Thực hiện tạo user và trả về DTO
        return helper.createUserEntity(dto);
    }

    // Cập nhật thông tin user
    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto) {
        User target = helper.findUserById(id);               // Tìm user cần sửa

        // Kiểm tra quyền sửa user này
        permissionProvider.canUpdateUser(
                currentUser(),
                target
        );

        // Ngăn TEAM_LEAD (và các role thấp hơn) gán role ADMIN hoặc TEAM_LEAD
        if (dto.getRoleCode() != null) {
            String desired = dto.getRoleCode();              // Role muốn gán
            String actorRole = currentUser().getRoleCode();  // Role của người đang thao tác
            if (("ADMIN".equals(desired) || "TEAM_LEAD".equals(desired)) && !"ADMIN".equals(actorRole)) {
                throw new RuntimeException("Chỉ ADMIN mới được gán role ADMIN/TEAM_LEAD");
            }
        }

        // Thực hiện cập nhật và trả về DTO
        return helper.updateUserEntity(id, dto);
    }

    // Xóa user
    @Override
    public void deleteUser(Long id) {
        User target = helper.findUserById(id);               // Tìm user cần xóa

        // Kiểm tra quyền xóa
        permissionProvider.canDeleteUser(
                currentUser(),
                target
        );

        // Thực hiện xóa
        helper.deleteUser(id);
    }

    // Endpoint đặc biệt chỉ dành cho ADMIN: gán role + department cho user bất kỳ
    @Override
    public UserResponseDTO assignRoleAndDepartment(
            Long userId,
            String roleCode,
            String departmentCode
    ) {
        // Chỉ ADMIN mới được gọi hàm này
        permissionProvider.canAssignRole(currentUser());

        // Tạo DTO tạm để tái sử dụng logic update
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setRoleCode(roleCode);
        dto.setDepartmentCode(departmentCode);

        // Gọi hàm update chung
        return helper.updateUserEntity(userId, dto);
    }
}
package com.example.admin_service.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Lombok: tự sinh getter cho toàn bộ field
@Getter
public class CustomUserPrincipal implements UserDetails {

    // ID của user trong database
    private final Long userId;

    // Username dùng để đăng nhập
    private final String username;

    // Password (đã được mã hóa - BCrypt)
    private final String password;

    // Mã role (ADMIN, USER, HR...)
    private final String roleCode;

    // Mã phòng ban (IT, HR, FINANCE...)
    private final String departmentCode;

    // Trạng thái hoạt động của user
    private final boolean active;

    // Danh sách quyền mà Spring Security hiểu (ROLE_ADMIN, ROLE_USER...)
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor để khởi tạo UserPrincipal
    public CustomUserPrincipal(
            Long userId,
            String username,
            String password,
            String roleCode,
            String departmentCode,
            boolean active,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleCode = roleCode;
        this.departmentCode = departmentCode;
        this.active = active;
        this.authorities = authorities;
    }

    // Kiểm tra user có phải ADMIN không
    public boolean isAdmin() {
        return "ADMIN".equals(this.roleCode);
    }

    // Kiểm tra user có phải TEAM_LEAD không
    public boolean isTeamLead() {
        return "TEAM_LEAD".equals(this.roleCode);
    }

    // Kiểm tra user có phải USER thường không
    public boolean isUser() {
        return "USER".equals(this.roleCode);
    }

    // Kiểm tra user có thuộc phòng ban không
    public boolean hasDepartment() {
        return this.departmentCode != null;
    }

    /* ================= UserDetails ================= */

    // Trả về danh sách quyền cho Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Tài khoản còn hạn không? (true = còn)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Tài khoản có bị khóa không? (true = không khóa)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // check mk còn hạn không ?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // User có được phép đăng nhập không?
    @Override
    public boolean isEnabled() {
        return active;
    }
}

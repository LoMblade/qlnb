package com.example.admin_service.security;

import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Logger dùng để ghi log (debug, error...)
    private static final Logger logger =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    // Repository để truy vấn user từ database
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security sẽ gọi hàm này khi user đăng nhập
    @Override
    public UserDetails loadUserByUsername(String username) {

        // If không tìm thấy user theo username → ném lỗi để Spring Security từ chối đăng nhập
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        // User bị khóa / inactive → không cho đăng nhập
        if (!user.getActive()) {
            throw new UsernameNotFoundException("User inactive");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Nếu user có role
        if (user.getRole() != null) {

            // Thêm role vào authorities
            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + user.getRole().getRoleCode()
                    )
            );
        }

        // Trả về UserDetails (CustomUserPrincipal)
        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole() != null
                        ? user.getRole().getRoleCode()
                        : null,                        // roleCode
                user.getDepartment() != null
                        ? user.getDepartment().getDepartmentCode()
                        : null,
                user.getActive(),
                authorities                           // danh sách quyền
        );
    }
}



package com.example.admin_service.config;
import com.example.admin_service.security.CustomPermissionEvaluator;
import com.example.admin_service.security.CustomUserDetailsService;
import com.example.admin_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// Bật bảo mật ở tầng method (dùng @PreAuthorize, hasPermission...)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomPermissionEvaluator permissionEvaluator;

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomPermissionEvaluator permissionEvaluator,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.permissionEvaluator = permissionEvaluator;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Tắt CSRF vì dùng JWT (stateless)
                .csrf(csrf -> csrf.disable())

                // Không dùng session (JWT là stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Dùng authenticationProvider tự custom
                .authenticationProvider(authenticationProvider())

                // Xử lý exception bảo mật
                .exceptionHandling(ex -> ex
                        // Khi chưa đăng nhập
                        .authenticationEntryPoint(
                                (req, res, e) -> res.sendError(401, "Unauthorized")
                        )
                        // Khi không đủ quyền
                        .accessDeniedHandler(
                                (req, res, e) -> res.sendError(403, "Access Denied")
                        )
                )

                // Phân quyền theo URL
                .authorizeHttpRequests(auth -> auth
                        // CÁC API PUBLIC (không cần đăng nhập)
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // TẤT CẢ REQUEST KHÁC PHẢI ĐĂNG NHẬP
                        .anyRequest().authenticated()
                )

                // Thêm filter JWT trước filter login mặc định
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // Build filter chain
        return http.build();
    }

    // Bean quản lý xác thực
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // Provider xác thực dùng UserDetailsService + PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Load user từ database
        provider.setUserDetailsService(userDetailsService);

        // So khớp mật khẩu
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
    // Bean mã hóa mật khẩu (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình xử lý biểu thức phân quyền (hasPermission)
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        // Gắn CustomPermissionEvaluator
        handler.setPermissionEvaluator(permissionEvaluator);

        return handler;
    }
}

    package com.example.admin_service.security;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;

    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.IOException;

    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

        private final JwtTokenProvider tokenProvider;
        private final CustomUserDetailsService userDetailsService;

        public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                       CustomUserDetailsService userDetailsService) {
            this.tokenProvider = tokenProvider;
            this.userDetailsService = userDetailsService;
        }

        /**
         * Bỏ qua filter cho các API public (login, register, refresh token)
         * Sử dụng getServletPath() để tránh lỗi context-path
         */
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getServletPath();
            return path.startsWith("/api/auth/");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring(7);

                    if (tokenProvider.validateToken(token)) {
                        String username = tokenProvider.getUsernameFromJWT(token);

                        var userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception ex) {
                    // Không chặn request, chỉ log để debug
                    logger.error("JWT authentication failed", ex);
                }
            }

            // Cho request đi tiếp, quyết định 401/403 để Spring Security xử lý
            filterChain.doFilter(request, response);
        }
    }

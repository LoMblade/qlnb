//package com.example.admin_service.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//public class SecurityUtils {
//
//    // Lấy username của user đang đăng nhập
//    public static String getCurrentUsername() {
//
//        // Lấy Authentication hiện tại từ SecurityContext
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        // Kiểm tra authentication tồn tại và principal là UserDetails
//        if (authentication != null
//                && authentication.getPrincipal() instanceof UserDetails) {
//
//            // Trả về username
//            return ((UserDetails) authentication.getPrincipal()).getUsername();
//        }
//
//        // Chưa đăng nhập hoặc không hợp lệ
//        return null;
//    }
//
//    public static Authentication getCurrentAuthentication() {
//
//        // Authentication chứa: principal, authorities, details
//        return SecurityContextHolder
//                .getContext()
//                .getAuthentication();
//    }
//}

//package com.example.admin_service.security;
//
//import com.example.admin_service.admin.entity.Admin;
//import com.example.admin_service.admin.repository.AdminRepository;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomAdminDetailsService implements UserDetailsService {
//
//    private final AdminRepository repo;
//
//    public CustomAdminDetailsService(AdminRepository repo) {
//        this.repo = repo;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Admin admin = repo.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(admin.getUsername())
//                .password(admin.getPassword())
//                .roles(admin.getRole().name())
//                .build();
//    }
//}

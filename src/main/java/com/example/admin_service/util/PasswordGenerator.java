package com.example.admin_service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để tạo BCrypt password hash
 * Chạy main method để tạo hash cho password "admin123"
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);
        
        // Verify
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("Password matches: " + matches);
    }
}


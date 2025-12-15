package com.example.admin_service.auth.controller;

import com.example.admin_service.user.entity.User;
import com.example.admin_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/check-password")
    public String checkPassword(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        
        if (user == null) {
            return "User not found";
        }
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        return String.format("User: %s, Password matches: %s, Active: %s", 
                username, matches, user.getActive());
    }
    
    @GetMapping("/generate-hash")
    public String generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        return "Password: " + password + "\nHash: " + hash;
    }
}


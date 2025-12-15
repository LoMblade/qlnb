package com.example.admin_service.user.controller;

import com.example.admin_service.security.CustomUserPrincipal;
import com.example.admin_service.user.dto.*;
import com.example.admin_service.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private CustomUserPrincipal principal() {
        return (CustomUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getCurrentRoleCode() {
        return principal().getRoleCode();
    }

    private String getCurrentDepartmentCode() {
        return principal().getDepartmentCode();
    }

    private String getCurrentUsername() {
        return principal().getUsername();
    }
}

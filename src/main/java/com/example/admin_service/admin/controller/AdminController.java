package com.example.admin_service.admin.controller;

import com.example.admin_service.admin.dto.AdminDTO;
import com.example.admin_service.admin.dto.AdminResponseDTO;
import com.example.admin_service.admin.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminDTO dto) {
        return ResponseEntity.ok(adminService.createAdmin(dto));
    }

}

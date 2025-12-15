package com.example.admin_service.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
}

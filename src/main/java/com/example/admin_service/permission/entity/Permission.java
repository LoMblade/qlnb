package com.example.admin_service.permission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false , name = "permission_code")
    private String permissionCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    public enum ResourceType {
        DEPARTMENT,
        USER,
        TEAM_LEAD,
        ROLE,
        PERMISSION
    }

    public enum ActionType {
        CREATE,
        READ,
        UPDATE,
        DELETE
    }
}


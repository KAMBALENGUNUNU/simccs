package com.acp.simccs.modules.identity.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String username;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(String action, String username, String details) {
        this.action = action;
        this.username = username;
        this.details = details;
    }
}

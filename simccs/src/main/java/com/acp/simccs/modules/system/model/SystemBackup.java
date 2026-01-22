package com.acp.simccs.modules.system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_backups")
@Data
@NoArgsConstructor
public class SystemBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "file_size_mb")
    private Double fileSizeMb;

    @Enumerated(EnumType.STRING)
    private ESystemStatus status;

    @Column(columnDefinition = "TEXT")
    private String logMessage; // Error details if failed

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SystemBackup(String filename, ESystemStatus status) {
        this.filename = filename;
        this.status = status;
    }
}
package com.acp.simccs.modules.system.controller;

import com.acp.simccs.modules.system.model.SystemBackup;
import com.acp.simccs.modules.system.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/backups")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArchiveController {

    @Autowired
    private BackupService backupService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemBackup>> getBackupHistory() {
        return ResponseEntity.ok(backupService.getBackupHistory());
    }

    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> triggerManualBackup() {
        // Run in background to avoid blocking response
        new Thread(() -> backupService.performManualBackup()).start();
        return ResponseEntity.ok("Backup process started in background.");
    }
}
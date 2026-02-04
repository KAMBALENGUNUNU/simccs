package com.acp.simccs.modules.system.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.system.model.SystemBackup;
import com.acp.simccs.modules.system.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/backups")
@RequiredArgsConstructor
public class ArchiveController {

    private final BackupService backupService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    // FIX: Explicitly defined List<SystemBackup> instead of wildcard
    public ResponseEntity<ResponseDTO<List<SystemBackup>>> getBackupHistory() {
        return ResponseDTO.success(backupService.getBackupHistory()).toResponseEntity();
    }

    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    // FIX: Changed return type to String (since we return a message, not a list)
    public ResponseEntity<ResponseDTO<String>> triggerManualBackup() {
        new Thread(backupService::performManualBackup).start();
        // FIX: Added .<String> type hint to handle the null data
        return ResponseDTO.<String>success("Backup process started in background.", null).toResponseEntity();
    }
}
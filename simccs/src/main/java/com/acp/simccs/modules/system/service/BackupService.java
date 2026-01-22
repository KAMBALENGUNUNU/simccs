package com.acp.simccs.modules.system.service;

import com.acp.simccs.modules.system.model.ESystemStatus;
import com.acp.simccs.modules.system.model.SystemBackup;
import com.acp.simccs.modules.system.repository.SystemBackupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    @Autowired
    private SystemBackupRepository backupRepository;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    private final String DB_NAME = "simccs_db";
    private final String BACKUP_DIR = "./backups/"; // Ensure this folder exists

    // Cron expression: At 02:00 AM every day
    @Scheduled(cron = "0 0 2 * * ?")
    public void performScheduledBackup() {
        logger.info("Starting scheduled backup...");
        performBackup("AUTO");
    }

    public void performManualBackup() {
        logger.info("Starting manual backup...");
        performBackup("MANUAL");
    }

    private void performBackup(String type) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + type + "_" + timestamp + ".sql";
        File backupFile = new File(BACKUP_DIR + filename);

        // Create directory if not exists
        new File(BACKUP_DIR).mkdirs();

        SystemBackup backupRecord = new SystemBackup(filename, ESystemStatus.IN_PROGRESS);
        backupRepository.save(backupRecord);

        try {
            // Command for PostgreSQL Dump
            // Note: pg_dump must be in your System PATH
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump",
                    "-U", dbUser,
                    "-F", "c", // Custom format (compressed)
                    "-b", // Include blobs
                    "-v", // Verbose
                    "-f", backupFile.getAbsolutePath(),
                    DB_NAME
            );

            // Set password environment variable
            pb.environment().put("PGPASSWORD", dbPass);

            Process process = pb.start();
            boolean finished = process.waitFor(60, TimeUnit.SECONDS);

            if (finished && process.exitValue() == 0) {
                backupRecord.setStatus(ESystemStatus.SUCCESS);
                backupRecord.setFileSizeMb(backupFile.length() / (1024.0 * 1024.0));
                backupRecord.setLogMessage("Backup completed successfully.");
            } else {
                backupRecord.setStatus(ESystemStatus.FAILED);
                backupRecord.setLogMessage("Process failed or timed out. Exit code: " + process.exitValue());
            }

        } catch (Exception e) {
            logger.error("Backup failed", e);
            backupRecord.setStatus(ESystemStatus.FAILED);
            backupRecord.setLogMessage(e.getMessage());
        }

        backupRepository.save(backupRecord);
    }

    public List<SystemBackup> getBackupHistory() {
        return backupRepository.findAllByOrderByCreatedAtDesc();
    }
}
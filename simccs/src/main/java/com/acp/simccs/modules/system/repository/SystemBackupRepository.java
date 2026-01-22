package com.acp.simccs.modules.system.repository;

import com.acp.simccs.modules.system.model.SystemBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemBackupRepository extends JpaRepository<SystemBackup, Long> {
    // Get latest backups first
    List<SystemBackup> findAllByOrderByCreatedAtDesc();
}
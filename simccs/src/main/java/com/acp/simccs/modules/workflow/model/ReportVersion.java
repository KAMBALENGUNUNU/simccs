package com.acp.simccs.modules.workflow.model;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.identity.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_versions")
@Data
@NoArgsConstructor
public class ReportVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private CrisisReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    private User editor; // Who made the change

    @Column(name = "content_snapshot_encrypted", columnDefinition = "TEXT")
    private String contentSnapshotEncrypted;

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
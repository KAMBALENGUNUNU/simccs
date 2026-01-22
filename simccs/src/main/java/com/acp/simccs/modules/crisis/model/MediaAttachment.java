package com.acp.simccs.modules.crisis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_attachments")
@Data
@NoArgsConstructor
public class MediaAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private CrisisReport report;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type")
    private String fileType; // image/jpeg, video/mp4

    @Column(name = "is_encrypted")
    private Boolean isEncrypted = true;
}
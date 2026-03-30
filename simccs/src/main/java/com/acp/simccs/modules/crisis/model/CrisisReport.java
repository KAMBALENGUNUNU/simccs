package com.acp.simccs.modules.crisis.model;

import com.acp.simccs.modules.identity.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class CrisisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_encrypted", columnDefinition = "TEXT", nullable = false)
    private String contentEncrypted;

    @Column(columnDefinition = "TEXT")
    private String summary; // Unencrypted preview for dashboards

    private Double locationLat;
    private Double locationLng;
    private String locationName;

    @Enumerated(EnumType.STRING)
    private EReportStatus status = EReportStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private EReportType reportType = EReportType.FEATURE;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private EPriority priority = EPriority.NORMAL;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Helper method to update timestamp before update
    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
package com.acp.simccs.modules.crisis.model;

import com.acp.simccs.modules.identity.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    private EReportStatus status = EReportStatus.DRAFT;

    @Column(name = "casualty_count")
    private Integer casualtyCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "report_categories",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();
    
    // Helper method to update timestamp before update
    @PreUpdate
    public void setLastUpdate() {  this.updatedAt = LocalDateTime.now(); }
}
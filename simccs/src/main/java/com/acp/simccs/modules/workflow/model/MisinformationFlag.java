package com.acp.simccs.modules.workflow.model;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.identity.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "misinformation_flags")
@Data
@NoArgsConstructor
public class MisinformationFlag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private CrisisReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagged_by_user_id")
    private User flaggedBy; // Null if system auto-flagged

    private String reason;
    
    // 0.0 to 1.0 confidence score (for future AI integration)
    @Column(name = "ai_confidence_score")
    private Double aiConfidenceScore; 

    private LocalDateTime createdAt = LocalDateTime.now();
}
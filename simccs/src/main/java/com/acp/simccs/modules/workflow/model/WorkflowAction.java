package com.acp.simccs.modules.workflow.model;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.identity.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_actions")
@Data
@NoArgsConstructor
public class WorkflowAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private CrisisReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor; // Editor or Admin

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private EWorkflowAction actionType;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
}
package com.acp.simccs.modules.workflow.service;

import com.acp.simccs.common.service.NotificationService; // <--- NEW IMPORT
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.modules.identity.model.ERole;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.workflow.dto.ReviewRequest;
import com.acp.simccs.modules.workflow.model.EWorkflowAction;
import com.acp.simccs.modules.workflow.model.WorkflowAction;
import com.acp.simccs.modules.workflow.repository.WorkflowActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {

    @Autowired
    private CrisisReportRepository reportRepository;

    @Autowired
    private WorkflowActionRepository workflowActionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.acp.simccs.modules.workflow.repository.ReportVersionRepository reportVersionRepository;

    @Autowired
    private com.acp.simccs.modules.workflow.service.MisinformationService misinformationService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void processReview(Long reportId, ReviewRequest request, String editorEmail) {
        CrisisReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User editor = userRepository.findByEmail(editorEmail)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

        // Security Check for PUBLISH
        if (request.getAction() == EWorkflowAction.PUBLISH) {
            boolean isAdmin = editor.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
            if (!isAdmin) {
                throw new RuntimeException("Unauthorized: Only Admins can publish reports.");
            }
        }

        // 1. Log the Action
        WorkflowAction action = new WorkflowAction();
        action.setReport(report);
        action.setActor(editor);
        action.setActionType(request.getAction());
        action.setComment(request.getComment());
        workflowActionRepository.save(action);

        // 2. Update Report Status based on Action
        if (request.getAction() == EWorkflowAction.APPROVE) {
            // Logic: If already Verified -> Publish. If Submitted -> Verify.
            if (report.getStatus() == EReportStatus.VERIFIED) {
                report.setStatus(EReportStatus.PUBLISHED);
            } else {
                report.setStatus(EReportStatus.VERIFIED);
            }
        } else if (request.getAction() == EWorkflowAction.REJECT) {
            report.setStatus(EReportStatus.REJECTED);
        } else if (request.getAction() == EWorkflowAction.REQUEST_REVISION) {
            report.setStatus(EReportStatus.DRAFT);
        } else if (request.getAction() == EWorkflowAction.PUBLISH) {
            report.setStatus(EReportStatus.PUBLISHED);
        }

        reportRepository.save(report);

        // 3. Send Notification (Email + WebSocket)
        if (report.getAuthor() != null) {
            notificationService.notifyReportStatusChange(
                    report.getAuthor().getEmail(),
                    report.getId(),
                    report.getStatus().name());
        }
    }

    public java.util.List<java.util.Map<String, Object>> getReportActions(Long reportId) {
        return workflowActionRepository.findByReportIdOrderByTimestampDesc(reportId).stream()
                .map(action -> {
                    java.util.Map<String, Object> dto = new java.util.HashMap<>();
                    dto.put("id", action.getId());
                    dto.put("timestamp", action.getTimestamp());
                    dto.put("actionType", action.getActionType());
                    dto.put("actorName", action.getActor() != null ? action.getActor().getFullName() : "Unknown");
                    dto.put("comment", action.getComment());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<java.util.Map<String, Object>> getVersions(Long reportId) {
        return ((java.util.List<?>) reportVersionRepository.findByReportId(reportId)).stream()
                .map(obj -> {
                    com.acp.simccs.modules.workflow.model.ReportVersion v = (com.acp.simccs.modules.workflow.model.ReportVersion) obj;
                    java.util.Map<String, Object> dto = new java.util.HashMap<>();
                    dto.put("id", v.getId());
                    dto.put("createdAt", v.getCreatedAt());
                    dto.put("changeReason", v.getChangeReason());
                    dto.put("actorName", v.getEditor() != null ? v.getEditor().getFullName() : "Unknown");
                    dto.put("versionNumber", v.getVersionNumber());
                    return (java.util.Map<String, Object>) dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.Map<String, Object> getVersion(Long versionId) {
        com.acp.simccs.modules.workflow.model.ReportVersion v = (com.acp.simccs.modules.workflow.model.ReportVersion) reportVersionRepository
                .findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        java.util.Map<String, Object> dto = new java.util.HashMap<>();
        dto.put("id", v.getId());
        dto.put("createdAt", v.getCreatedAt());
        dto.put("changeReason", v.getChangeReason());
        dto.put("actorName", v.getEditor() != null ? v.getEditor().getFullName() : "Unknown");
        dto.put("contentSnapshotEncrypted", v.getContentSnapshotEncrypted());
        dto.put("versionNumber", v.getVersionNumber());
        return dto;
    }

    public void flagReport(Long reportId) {
        CrisisReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        misinformationService.manuallyFlagReport(report);
    }

    public java.util.List<?> getFlaggedReports() {
        return misinformationService.getFlaggedReports();
    }
}
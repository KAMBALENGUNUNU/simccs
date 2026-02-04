package com.acp.simccs.modules.workflow.service;

import com.acp.simccs.common.service.NotificationService; // <--- NEW IMPORT
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
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
    private NotificationService notificationService; // <--- REPLACED SimpMessagingTemplate

    @Transactional
    public void processReview(Long reportId, ReviewRequest request, String editorEmail) {
        CrisisReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User editor = userRepository.findByEmail(editorEmail)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

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
        }

        reportRepository.save(report);

        // 3. Send Notification (Email + WebSocket)
        if (report.getAuthor() != null) {
            notificationService.notifyReportStatusChange(
                    report.getAuthor().getEmail(),
                    report.getId(),
                    report.getStatus().name()
            );
        }
    }

    public java.util.List<?> getVersions(Long reportId) {
        return reportVersionRepository.findByReportId(reportId);
    }

    public Object getVersion(Long versionId) {
        return reportVersionRepository.findById(versionId).orElseThrow(() -> new RuntimeException("Version not found"));
    }

    public void flagReport(Long reportId) {
        CrisisReport report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Report not found"));
        misinformationService.manuallyFlagReport(report);
    }

    public java.util.List<?> getFlaggedReports() {
        return misinformationService.getFlaggedReports();
    }
}
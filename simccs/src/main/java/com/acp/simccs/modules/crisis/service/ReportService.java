package com.acp.simccs.modules.crisis.service;

import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.dto.ReportResponse;
import com.acp.simccs.modules.crisis.event.ReportSubmittedEvent;
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import com.acp.simccs.modules.crisis.model.EReportType;
import com.acp.simccs.modules.crisis.model.EPriority;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.security.SecurityService;
import com.acp.simccs.modules.communication.service.ChatService;
import com.acp.simccs.modules.workflow.service.MisinformationService;
import com.acp.simccs.modules.workflow.model.ReportVersion;
import com.acp.simccs.modules.workflow.repository.ReportVersionRepository;
import com.acp.simccs.modules.crisis.model.MediaAttachment;
import com.acp.simccs.modules.crisis.repository.MediaAttachmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportVersionRepository reportVersionRepository;
    @Autowired
    private MediaAttachmentRepository mediaAttachmentRepository;
    @Autowired
    private CrisisReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private MisinformationService misinformationService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MinioStorageService minioStorageService;

    // --- UPDATED PRODUCTION SEARCH METHOD ---
    public List<ReportResponse> searchReports(String statusStr, Long authorId) {
        EReportStatus status = null;

        // 1. Safe String -> Enum conversion
        // We accept "all", null, or empty string as "no filter"
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("all")) {
            try {
                status = EReportStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If invalid status provided, we ignore the filter (or you could throw
                // exception)
                status = null;
            }
        }

        // 2. Call the optimized Repository Query
        List<CrisisReport> reports = reportRepository.searchReports(status, authorId);

        // 3. Map to DTO
        return reports.stream()
                .map(r -> mapToResponse(r, false)) // false = keep encrypted for list views
                .collect(Collectors.toList());
    }
    // -------------------------

    @Transactional
    public ReportResponse createReport(ReportRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CrisisReport report = new CrisisReport();
        report.setAuthor(author);
        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setLocationLat(request.getLatitude());
        report.setLocationLng(request.getLongitude());
        report.setLocationName(request.getLocationName());
        report.setStatus(EReportStatus.SUBMITTED);

        // Parse and assign report type
        try {
            report.setReportType(request.getReportType() != null
                    ? EReportType.valueOf(request.getReportType().toUpperCase())
                    : EReportType.FEATURE);
        } catch (IllegalArgumentException e) {
            report.setReportType(EReportType.FEATURE);
        }

        // Parse and assign priority
        try {
            report.setPriority(request.getPriority() != null
                    ? EPriority.valueOf(request.getPriority().toUpperCase())
                    : EPriority.NORMAL);
        } catch (IllegalArgumentException e) {
            report.setPriority(EPriority.NORMAL);
        }

        // Encrypt content
        report.setContentEncrypted(securityService.encrypt(request.getContent()));

        // Save first to generate ID
        CrisisReport savedReport = reportRepository.save(report);

        // Auto-create chat channel
        chatService.createReportChannel(savedReport.getId(), savedReport.getTitle());

        // Handle Media Attachments
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            for (String fileName : request.getMediaFiles()) {
                MediaAttachment attachment = new MediaAttachment();
                attachment.setReport(savedReport);
                attachment.setFilePath(fileName);
                attachment.setFileType("unknown");
                attachment.setIsEncrypted(false);
                mediaAttachmentRepository.save(attachment);
            }
        }

        // AI Scan & Event
        misinformationService.autoScanReport(savedReport);
        eventPublisher.publishEvent(new ReportSubmittedEvent(savedReport));

        return mapToResponse(savedReport, true);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(r -> mapToResponse(r, false))
                .collect(Collectors.toList());
    }

    public ReportResponse getReportById(Long id) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToResponse(report, true);
    }

    @Transactional
    public ReportResponse updateReport(Long id, ReportRequest request, String userEmail) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isEditor = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == com.acp.simccs.modules.identity.model.ERole.ROLE_EDITOR);

        EReportStatus status = report.getStatus();

        // Rule A
        if ((status == EReportStatus.SUBMITTED || status == EReportStatus.VERIFIED) && !isEditor) {
            throw new RuntimeException("Unauthorized: Once submitted, only an Editor can modify this report.");
        }

        // Rule B: Only draft/rejected editors can edit, UNLESS they are an editor
        if (!isEditor && (status == EReportStatus.DRAFT || status == EReportStatus.REJECTED)
                && !currentUser.getId().equals(report.getAuthor().getId())) {
            throw new RuntimeException("Unauthorized: You can only edit your own drafts.");
        }

        // Versioning Logic
        ReportVersion version = new ReportVersion();
        version.setReport(report);
        version.setEditor(currentUser);
        version.setContentSnapshotEncrypted(report.getContentEncrypted());
        version.setChangeReason("Update request by " + userEmail);

        int currentCount = reportVersionRepository.findByReportId(id).size();
        version.setVersionNumber(currentCount + 1);

        reportVersionRepository.save(version);

        // Update Fields
        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setLocationLat(request.getLatitude());
        report.setLocationLng(request.getLongitude());
        report.setLocationName(request.getLocationName());
        report.setContentEncrypted(securityService.encrypt(request.getContent()));

        // Update report type and priority if provided
        if (request.getReportType() != null) {
            try {
                report.setReportType(EReportType.valueOf(request.getReportType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (request.getPriority() != null) {
            try {
                report.setPriority(EPriority.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Handle Media Attachments on Update
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            List<MediaAttachment> existing = mediaAttachmentRepository.findByReportId(report.getId());
            Set<String> existingPaths = existing.stream().map(MediaAttachment::getFilePath).collect(Collectors.toSet());

            // Check if user is trying to add NEW media
            boolean addingNewMedia = request.getMediaFiles().stream().anyMatch(f -> !existingPaths.contains(f));

            if (addingNewMedia && !isEditor) {
                // If not an editor, only allow new image uploads if the report is in DRAFT
                // state
                if (status != EReportStatus.DRAFT && status != EReportStatus.REJECTED) {
                    throw new RuntimeException(
                            "Unauthorized: Journalists can only upload images to DRAFT or REJECTED reports.");
                }
            }

            for (String fileName : request.getMediaFiles()) {
                if (!existingPaths.contains(fileName)) {
                    MediaAttachment attachment = new MediaAttachment();
                    attachment.setReport(report);
                    attachment.setFilePath(fileName);
                    attachment.setFileType("unknown");
                    attachment.setIsEncrypted(false);
                    mediaAttachmentRepository.save(attachment);
                }
            }
        }

        return mapToResponse(reportRepository.save(report), true);
    }

    @Transactional
    public void softDeleteReport(Long id) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(EReportStatus.DELETED);
        reportRepository.save(report);
    }

    @Autowired
    private com.acp.simccs.modules.workflow.repository.MisinformationFlagRepository misinformationFlagRepository;

    private ReportResponse mapToResponse(CrisisReport report, boolean decryptContent) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setTitle(report.getTitle());
        response.setSummary(report.getSummary());
        response.setAuthorName(report.getAuthor().getFullName());
        response.setAuthorId(report.getAuthor().getId());
        response.setStatus(report.getStatus().name());
        response.setLatitude(report.getLocationLat());
        response.setLongitude(report.getLocationLng());
        response.setLocationName(report.getLocationName());
        response.setReportType(
                report.getReportType() != null ? report.getReportType().name() : EReportType.FEATURE.name());
        response.setPriority(report.getPriority() != null ? report.getPriority().name() : EPriority.NORMAL.name());
        response.setCreatedAt(report.getCreatedAt());

        // Check if report is flagged
        response.setFlagged(!misinformationFlagRepository.findByReportId(report.getId()).isEmpty());

        if (decryptContent) {
            response.setContent(securityService.decrypt(report.getContentEncrypted()));
        } else {
            response.setContent("Encrypted content... view details to read.");
        }

        List<MediaAttachment> attachments = mediaAttachmentRepository.findByReportId(report.getId());
        if (attachments != null && !attachments.isEmpty()) {
            List<String> mediaFiles = attachments.stream()
                    .map(a -> minioStorageService.getFileUrl(a.getFilePath()))
                    .collect(Collectors.toList());
            response.setMediaFiles(mediaFiles);
        }

        return response;
    }
}
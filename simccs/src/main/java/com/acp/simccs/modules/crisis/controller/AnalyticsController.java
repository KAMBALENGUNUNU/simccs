package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.modules.crisis.dto.PersonnelStatsDTO;
import com.acp.simccs.modules.crisis.dto.RiskAuditDTO;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import com.acp.simccs.modules.identity.model.ERole;
import com.acp.simccs.modules.identity.model.Role;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.workflow.model.EWorkflowAction;
import com.acp.simccs.modules.workflow.model.WorkflowAction;
import com.acp.simccs.modules.workflow.model.MisinformationFlag;
import com.acp.simccs.modules.workflow.repository.MisinformationFlagRepository;
import com.acp.simccs.modules.workflow.repository.WorkflowActionRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Crisis analytics")
public class AnalyticsController {

        private final CrisisReportRepository crisisReportRepository;
        private final MisinformationFlagRepository misinformationFlagRepository;
        private final UserRepository userRepository;
        private final WorkflowActionRepository workflowActionRepository;

        @GetMapping("/dashboard")
        public ResponseEntity<ResponseDTO<Map<String, Object>>> getDashboardStats() {
                List<CrisisReport> allReports = crisisReportRepository.findAll().stream()
                                .filter(r -> r.getStatus() != EReportStatus.DELETED)
                                .collect(Collectors.toList());
                Map<String, Object> stats = new HashMap<>();

                // 1. Basic Counters
                stats.put("totalReports", allReports.size());
                stats.put("totalCasualties",
                                allReports.stream()
                                                .mapToInt(r -> r.getCasualtyCount() == null ? 0 : r.getCasualtyCount())
                                                .sum());

                // 2. Count by Status (Mutually Exclusive for Dashboard)
                // Pending = DRAFT + SUBMITTED
                stats.put("pendingReports", allReports.stream()
                                .filter(r -> r.getStatus() == EReportStatus.SUBMITTED
                                                || r.getStatus() == EReportStatus.DRAFT)
                                .count());

                // Approved = VERIFIED + PUBLISHED
                stats.put("approvedReports", allReports.stream()
                                .filter(r -> r.getStatus() == EReportStatus.VERIFIED
                                                || r.getStatus() == EReportStatus.PUBLISHED)
                                .count());

                stats.put("rejectedReports", allReports.stream()
                                .filter(r -> r.getStatus() == EReportStatus.REJECTED).count());

                long flaggedCount = misinformationFlagRepository.findAll().stream()
                                .filter(flag -> flag.getReport() != null
                                                && flag.getReport().getStatus() != EReportStatus.DELETED)
                                .map(flag -> flag.getReport().getId())
                                .distinct()
                                .count();
                stats.put("flaggedReports", flaggedCount);

                // 3. Reports by Status Map (for charts)
                Map<String, Long> reportsByStatus = allReports.stream()
                                .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting()));
                stats.put("reportsByStatus", reportsByStatus);

                // 4. Reports by Category
                Map<String, Long> reportsByCategory = new HashMap<>();
                allReports.forEach(report -> {
                        report.getCategories().forEach(category -> {
                                reportsByCategory.put(category.getName(),
                                                reportsByCategory.getOrDefault(category.getName(), 0L) + 1);
                        });
                });
                stats.put("reportsByCategory", reportsByCategory);

                // 5. Recent Activity (Last 5 reports)
                List<Map<String, Object>> recentActivity = allReports.stream()
                                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                                .limit(5)
                                .map(r -> {
                                        Map<String, Object> activity = new HashMap<>();
                                        activity.put("id", r.getId());
                                        activity.put("title", r.getTitle());
                                        activity.put("status", r.getStatus().name());
                                        activity.put("createdAt", r.getCreatedAt());
                                        return activity;
                                })
                                .collect(Collectors.toList());
                stats.put("recentActivity", recentActivity);

                return ResponseDTO.success(stats).toResponseEntity();
        }

        @GetMapping("/personnel")
        public ResponseEntity<ResponseDTO<PersonnelStatsDTO>> getPersonnelStats() {
                PersonnelStatsDTO stats = new PersonnelStatsDTO();
                List<User> allUsers = userRepository.findAll();
                List<CrisisReport> allReports = crisisReportRepository.findAll();
                List<WorkflowAction> allActions = workflowActionRepository.findAll();

                // 1. Calculate Journalist Stats
                List<PersonnelStatsDTO.JournalistStat> journalists = new ArrayList<>();
                List<User> journalistUsers = allUsers.stream()
                                .filter(u -> u.getRoles().stream()
                                                .anyMatch(role -> role.getName() == ERole.ROLE_JOURNALIST))
                                .toList();

                for (User journalist : journalistUsers) {
                        List<CrisisReport> userReports = allReports.stream()
                                        .filter(r -> r.getAuthor() != null
                                                        && r.getAuthor().getId().equals(journalist.getId()))
                                        .toList();

                        long total = userReports.size();
                        long verified = userReports.stream()
                                        .filter(r -> r.getStatus() == EReportStatus.VERIFIED
                                                        || r.getStatus() == EReportStatus.PUBLISHED)
                                        .count();
                        long rejected = userReports.stream()
                                        .filter(r -> r.getStatus() == EReportStatus.REJECTED)
                                        .count();

                        double accuracy = total > 0 ? (double) verified / total : 0.0;

                        PersonnelStatsDTO.JournalistStat stat = new PersonnelStatsDTO.JournalistStat();
                        stat.setId(journalist.getId());
                        stat.setName(journalist.getFullName() != null ? journalist.getFullName()
                                        : journalist.getEmail());
                        stat.setEmail(journalist.getEmail());
                        stat.setTotalReports(total);
                        stat.setVerifiedReports(verified);
                        stat.setRejectedReports(rejected);
                        stat.setAccuracyRate(accuracy * 100);
                        journalists.add(stat);
                }
                stats.setJournalists(journalists);

                // 2. Calculate Editor Stats
                List<PersonnelStatsDTO.EditorStat> editors = new ArrayList<>();
                List<User> editorUsers = allUsers.stream()
                                .filter(u -> u.getRoles().stream()
                                                .anyMatch(role -> role.getName() == ERole.ROLE_EDITOR))
                                .toList();

                for (User editor : editorUsers) {
                        List<WorkflowAction> editorActions = allActions.stream()
                                        .filter(a -> a.getActor() != null
                                                        && a.getActor().getId().equals(editor.getId()))
                                        .toList();

                        long totalReviews = editorActions.stream()
                                        .filter(a -> a.getActionType() == EWorkflowAction.APPROVE
                                                        || a.getActionType() == EWorkflowAction.REJECT
                                                        || a.getActionType() == EWorkflowAction.REQUEST_REVISION)
                                        .count();

                        long flagsRaised = editorActions.stream()
                                        .filter(a -> a.getActionType() == EWorkflowAction.FLAG_MISINFORMATION)
                                        .count();

                        PersonnelStatsDTO.EditorStat stat = new PersonnelStatsDTO.EditorStat();
                        stat.setId(editor.getId());
                        stat.setName(editor.getFullName() != null ? editor.getFullName() : editor.getEmail());
                        stat.setEmail(editor.getEmail());
                        stat.setTotalReviews(totalReviews);
                        stat.setFlagsRaised(flagsRaised);
                        stat.setAverageReviewTimeHours(0.0); // Simple default for now
                        editors.add(stat);
                }
                stats.setEditors(editors);

                return ResponseDTO.success(stats).toResponseEntity();
        }

        @GetMapping("/risk-audit")
        public ResponseEntity<ResponseDTO<RiskAuditDTO>> getRiskAudit() {
                RiskAuditDTO audit = new RiskAuditDTO();

                var flags = misinformationFlagRepository.findAll().stream()
                                .filter(f -> f.getReport() != null
                                                && f.getReport().getStatus() != EReportStatus.DELETED)
                                .toList();

                List<RiskAuditDTO.FlaggedReportItem> items = new ArrayList<>();
                for (var flag : flags) {
                        RiskAuditDTO.FlaggedReportItem item = new RiskAuditDTO.FlaggedReportItem();
                        item.setReportId(flag.getReport().getId());
                        item.setReportTitle(flag.getReport().getTitle());
                        item.setAuthorName(
                                        flag.getReport().getAuthor() != null
                                                        ? (flag.getReport().getAuthor().getFullName() != null
                                                                        ? flag.getReport().getAuthor().getFullName()
                                                                        : flag.getReport().getAuthor().getEmail())
                                                        : "Unknown");
                        item.setFlaggedBy(flag.getFlaggedBy() != null ? flag.getFlaggedBy().getEmail() : "System");
                        item.setReason(flag.getReason());
                        item.setFlaggedAt(flag.getCreatedAt());
                        items.add(item);
                }

                audit.setFlaggedReports(items);
                audit.setTotalFlags(flags.size());
                audit.setUniqueReportsFlagged(flags.stream().map(f -> f.getReport().getId()).distinct().count());

                return ResponseDTO.success(audit).toResponseEntity();
        }
}
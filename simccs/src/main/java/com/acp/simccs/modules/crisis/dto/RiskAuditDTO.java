package com.acp.simccs.modules.crisis.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RiskAuditDTO {
    private List<FlaggedReportItem> flaggedReports;
    private long totalFlags;
    private long uniqueReportsFlagged;

    @Data
    public static class FlaggedReportItem {
        private Long reportId;
        private String reportTitle;
        private String authorName;
        private String flaggedBy;
        private String reason;
        private LocalDateTime flaggedAt;
    }
}

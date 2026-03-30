package com.acp.simccs.modules.crisis.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String authorName;
    private Long authorId;
    private String status;
    private Double latitude;
    private Double longitude;
    private String locationName;
    /** e.g. "BREAKING", "EXCLUSIVE", "PRESS_RELEASE", "FEATURE" */
    private String reportType;
    /** e.g. "URGENT", "HIGH", "NORMAL", "LOW" */
    private String priority;
    private LocalDateTime createdAt;
    private java.util.List<String> mediaFiles;
    private boolean flagged;
}

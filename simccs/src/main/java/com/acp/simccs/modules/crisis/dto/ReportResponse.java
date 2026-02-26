package com.acp.simccs.modules.crisis.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ReportResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String authorName;
    private String status;
    private Double latitude;
    private Double longitude;
    private Integer casualtyCount;
    private LocalDateTime createdAt;
    private Set<String> categories;
}
package com.acp.simccs.modules.crisis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content; // Plain text — encrypted by the service before persisting

    @NotBlank
    private String summary;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String locationName;

    /** Editorial classification: BREAKING | EXCLUSIVE | PRESS_RELEASE | FEATURE */
    private String reportType;

    /** Dispatch priority: URGENT | HIGH | NORMAL | LOW */
    private String priority;

    private java.util.List<String> mediaFiles;
}
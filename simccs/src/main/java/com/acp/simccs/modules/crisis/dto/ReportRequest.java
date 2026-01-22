package com.acp.simccs.modules.crisis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class ReportRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content; // This is the PLAIN TEXT content coming from the user

    @NotBlank
    private String summary;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Integer casualtyCount;
    
    private Set<String> categories; // e.g. ["Fire", "Riot"]
}
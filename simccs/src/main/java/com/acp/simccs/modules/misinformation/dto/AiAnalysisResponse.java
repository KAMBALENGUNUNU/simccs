package com.acp.simccs.modules.misinformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the expected JSON structure from the AI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisResponse {

    private Double confidenceScore;
    private String reason;
    private String searchQuery;
    private java.util.List<FactCheckHit> factCheckHits;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactCheckHit {
        private String claim;
        private String rating;
        private String sourceUrl;
        private String publisher;
    }
}

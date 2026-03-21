package com.acp.simccs.modules.misinformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the expected JSON structure from the AI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {

    private Double confidenceScore;
    private String reason;
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

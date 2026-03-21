package com.acp.simccs.modules.misinformation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactCheckResponse {
    private List<Claim> claims;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Claim {
        private String text;
        private String claimant;
        private String claimDate;
        private List<ClaimReview> claimReview;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimReview {
        private Publisher publisher;
        private String url;
        private String title;
        private String reviewDate;
        private String textualRating;
        private String languageCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Publisher {
        private String name;
        private String site;
    }
}

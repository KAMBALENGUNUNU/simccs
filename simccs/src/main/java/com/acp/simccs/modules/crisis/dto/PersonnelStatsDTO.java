package com.acp.simccs.modules.crisis.dto;

import lombok.Data;
import java.util.List;

@Data
public class PersonnelStatsDTO {
    private List<JournalistStat> journalists;
    private List<EditorStat> editors;

    @Data
    public static class JournalistStat {
        private Long id;
        private String name;
        private String email;
        private long totalReports;
        private long verifiedReports;
        private long rejectedReports;
        private double accuracyRate; // verified / (verified + rejected)
    }

    @Data
    public static class EditorStat {
        private Long id;
        private String name;
        private String email;
        private long totalReviews;
        private long flagsRaised;
        private double averageReviewTimeHours;
    }
}

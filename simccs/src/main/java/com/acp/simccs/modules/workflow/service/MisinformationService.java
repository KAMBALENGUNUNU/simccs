package com.acp.simccs.modules.workflow.service;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisResponse;
import com.acp.simccs.modules.misinformation.service.AiMisinformationService;
import com.acp.simccs.modules.workflow.model.MisinformationFlag;
import com.acp.simccs.modules.workflow.repository.MisinformationFlagRepository;
// FIX: Import from the new location
import com.acp.simccs.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MisinformationService {

    @Autowired
    private MisinformationFlagRepository flagRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AiMisinformationService aiMisinformationService;

    @Autowired
    private CrisisReportRepository crisisReportRepository;

    @Async
    public void autoScanReport(CrisisReport report) {
        String clearText;
        try {
            clearText = securityService.decrypt(report.getContentEncrypted());
        } catch (Exception e) {
            log.error("Failed to decrypt report content for misinformation scan.", e);
            return;
        }

        try {
            AiAnalysisResponse response = aiMisinformationService.analyzeReport(clearText);

            if (response != null && response.getConfidenceScore() != null && response.getConfidenceScore() > 0.65) {
                createSystemFlag(report, response.getReason(), response.getConfidenceScore());
            }
        } catch (Exception e) {
            log.error(
                    "AI misinformation analysis API call failed or timed out. Report will not be flagged automatically.",
                    e);
        }
    }

    private void createSystemFlag(CrisisReport report, String reason, Double confidence) {
        MisinformationFlag flag = new MisinformationFlag();
        flag.setReport(report);
        flag.setFlaggedBy(null);
        flag.setReason(reason);
        flag.setAiConfidenceScore(confidence);
        flagRepository.save(flag);
    }

    public void manuallyFlagReport(CrisisReport report) {
        createSystemFlag(report, "Manually flagged by Editor/Admin", 1.0);
    }

    public AiAnalysisResponse checkReportOnDemand(Long reportId) {
        CrisisReport report = crisisReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        String clearText = securityService.decrypt(report.getContentEncrypted());

        return aiMisinformationService.analyzeReport(clearText);
    }

    public List<MisinformationFlag> getFlaggedReports() {
        return flagRepository.findAll();
    }
}
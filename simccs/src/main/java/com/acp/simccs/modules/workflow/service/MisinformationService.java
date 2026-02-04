package com.acp.simccs.modules.workflow.service;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.workflow.model.MisinformationFlag;
import com.acp.simccs.modules.workflow.repository.MisinformationFlagRepository;
// FIX: Import from the new location
import com.acp.simccs.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MisinformationService {

    @Autowired
    private MisinformationFlagRepository flagRepository;

    @Autowired
    private SecurityService securityService;

    private final List<String> SUSPICIOUS_KEYWORDS = Arrays.asList(
            "unconfirmed", "rumor", "magic", "fake", "hoax", "conspiracy", "alien"
    );

    @Async
    public void autoScanReport(CrisisReport report) {
        // Fix: Use the service to decrypt
        String clearText = securityService.decrypt(report.getContentEncrypted()).toLowerCase();

        for (String word : SUSPICIOUS_KEYWORDS) {
            if (clearText.contains(word)) {
                createSystemFlag(report, "AI detected suspicious keyword: " + word, 0.85);
                return;
            }
        }

        if (report.getCasualtyCount() > 1000) {
            createSystemFlag(report, "AI detected unusually high casualty count", 0.60);
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

    public List<MisinformationFlag> getFlaggedReports() {
        return flagRepository.findAll();
    }
}
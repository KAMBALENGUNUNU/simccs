package com.acp.simccs.modules.workflow.service;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.identity.service.SecurityService;
import com.acp.simccs.modules.workflow.model.MisinformationFlag;
import com.acp.simccs.modules.workflow.repository.MisinformationFlagRepository;
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

    // A simple list of suspicious words for our "AI" to detect
    private final List<String> SUSPICIOUS_KEYWORDS = Arrays.asList(
            "unconfirmed", "rumor", "magic", "fake", "hoax", "conspiracy", "alien"
    );

    @Async // Run in background so the user doesn't have to wait
    public void autoScanReport(CrisisReport report) {
        // 1. Decrypt the content to analyze it
        String clearText = securityService.decrypt(report.getContentEncrypted()).toLowerCase();
        
        // 2. Scan for keywords
        for (String word : SUSPICIOUS_KEYWORDS) {
            if (clearText.contains(word)) {
                // FLAGGED!
                createSystemFlag(report, "AI detected suspicious keyword: " + word, 0.85);
                return; // Stop after first hit (or remove to find all)
            }
        }
        
        // 3. (Optional) Check casualty count anomalies
        if (report.getCasualtyCount() > 1000) {
             createSystemFlag(report, "AI detected unusually high casualty count", 0.60);
        }
    }

    private void createSystemFlag(CrisisReport report, String reason, Double confidence) {
        MisinformationFlag flag = new MisinformationFlag();
        flag.setReport(report);
        flag.setFlaggedBy(null); // Null means "System/AI"
        flag.setReason(reason);
        flag.setAiConfidenceScore(confidence);
        flagRepository.save(flag);
    }
}
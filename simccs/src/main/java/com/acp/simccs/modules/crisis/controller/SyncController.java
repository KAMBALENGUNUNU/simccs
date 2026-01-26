package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
@Tag(name = "Sync", description = "Offline mode synchronization")
public class SyncController {

    @Autowired
    ReportService reportService;

    @PostMapping("/batch")
    public ResponseEntity<String> syncBatch(@RequestBody List<ReportRequest> reports) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        int successCount = 0;
        for (ReportRequest req : reports) {
            try {
                reportService.createReport(req, email);
                successCount++;
            } catch (Exception e) {
                // Log error
            }
        }
        return ResponseEntity.ok("Synced " + successCount + " reports.");
    }
}

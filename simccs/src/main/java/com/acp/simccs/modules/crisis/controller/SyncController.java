package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "Offline mode synchronization")
public class SyncController {

    private final ReportService reportService;

    @PostMapping("/batch")
    public ResponseEntity<ResponseDTO<String>> syncBatch(@RequestBody List<ReportRequest> reports) {
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
        return ResponseDTO.<String>success("Synced " + successCount + " reports.", null).toResponseEntity();    }
}
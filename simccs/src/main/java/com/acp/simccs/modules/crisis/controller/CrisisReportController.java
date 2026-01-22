package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.dto.ReportResponse;
import com.acp.simccs.modules.crisis.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CrisisReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> submitReport(@Valid @RequestBody ReportRequest request) {
        // Get current user from Security Context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        ReportResponse response = reportService.createReport(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }
}
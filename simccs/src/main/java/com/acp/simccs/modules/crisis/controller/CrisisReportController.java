package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.dto.ReportResponse;
import com.acp.simccs.modules.crisis.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Crisis Reports", description = "Crisis reporting and management endpoints")

public class CrisisReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
        @Operation(
        summary = "Submit crisis report",
        description = "Create a new crisis report. Accessible to JOURNALIST and ADMIN roles.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ReportResponse> submitReport(@Valid @RequestBody ReportRequest request) {
        // Get current user from Security Context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        ReportResponse response = reportService.createReport(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
        @Operation(
        summary = "Get all crisis reports",
        description = "Retrieve all crisis reports. Accessible to USER, JOURNALIST, EDITOR, and ADMIN roles.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
        @Operation(
        summary = "Get report by ID",
        description = "Retrieve a specific crisis report by ID. Accessible to EDITOR and ADMIN roles.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Update a report", description = "Update an existing report (creates new version).")
    public ResponseEntity<ReportResponse> updateReport(@PathVariable Long id, @Valid @RequestBody ReportRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reportService.updateReport(id, request, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a report", description = "Soft delete a report.")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.softDeleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
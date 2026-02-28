package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.common.dto.ResponseDTO;
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
    @Operation(summary = "Submit crisis report", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ResponseDTO<ReportResponse>> submitReport(@Valid @RequestBody ReportRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ReportResponse response = reportService.createReport(request, email);
        return ResponseDTO.success("Report created successfully", response).toResponseEntity();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Get all crisis reports", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ResponseDTO<List<ReportResponse>>> getAllReports() {
        return ResponseDTO.success(reportService.getAllReports()).toResponseEntity();
    }

    // --- UPDATED SEARCH ENDPOINT ---
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Search reports by status or author", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ResponseDTO<List<ReportResponse>>> searchReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId) {

        // Logic delegates to the Service's robust search method
        List<ReportResponse> results = reportService.searchReports(status, authorId);

        return ResponseDTO.success(results).toResponseEntity();
    }
    // -------------------------------

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Get report by ID", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ResponseDTO<ReportResponse>> getReportById(@PathVariable Long id) {
        return ResponseDTO.success(reportService.getReportById(id)).toResponseEntity();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('EDITOR')")
    @Operation(summary = "Update a report")
    public ResponseEntity<ResponseDTO<ReportResponse>> updateReport(@PathVariable Long id,
            @Valid @RequestBody ReportRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseDTO.success(reportService.updateReport(id, request, email)).toResponseEntity();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a report")
    public ResponseEntity<ResponseDTO<Object>> deleteReport(@PathVariable Long id) {
        reportService.softDeleteReport(id);
        return ResponseDTO.success("Report deleted successfully", null).toResponseEntity();
    }
}
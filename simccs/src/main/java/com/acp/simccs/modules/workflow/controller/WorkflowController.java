package com.acp.simccs.modules.workflow.controller;

import com.acp.simccs.modules.workflow.dto.ReviewRequest;
import com.acp.simccs.modules.workflow.service.WorkflowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Workflow", description = "Report review and workflow management endpoints")

public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Change report status", description = "Approve, Reject, or Request Revision.")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        String editorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        workflowService.processReview(id, request, editorEmail);
        return ResponseEntity.ok("Status changed to: " + request.getAction());
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getVersions(id));
    }

    @GetMapping("/{id}/versions/{vId}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersionDetail(@PathVariable Long id, @PathVariable Long vId) {
        return ResponseEntity.ok(workflowService.getVersion(vId));
    }

    @PostMapping("/{id}/flag")
    public ResponseEntity<?> flagReport(@PathVariable Long id) {
        workflowService.flagReport(id);
        return ResponseEntity.ok("Report flagged for misinformation.");
    }

    @GetMapping("/flagged")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getFlaggedReports() {
        return ResponseEntity.ok(workflowService.getFlaggedReports());
    }
}
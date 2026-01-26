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
// CHANGED: Distinguish this path from CrisisReportController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Workflow", description = "Report review and workflow management endpoints")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    // CHANGED: Adjusted paths to match the new base URL logic
    // URL becomes: /api/workflow/reports/{id}/status
    @PostMapping("/reports/{id}/status")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Change report status", description = "Approve, Reject, or Request Revision.")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        String editorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        workflowService.processReview(id, request, editorEmail);
        return ResponseEntity.ok("Status changed to: " + request.getAction());
    }

    // URL becomes: /api/workflow/reports/{id}/versions
    @GetMapping("/reports/{id}/versions")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getVersions(id));
    }

    // URL becomes: /api/workflow/reports/{id}/versions/{vId}
    @GetMapping("/reports/{id}/versions/{vId}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersionDetail(@PathVariable Long id, @PathVariable Long vId) {
        return ResponseEntity.ok(workflowService.getVersion(vId));
    }

    // URL becomes: /api/workflow/reports/{id}/flag
    @PostMapping("/reports/{id}/flag")
    public ResponseEntity<?> flagReport(@PathVariable Long id) {
        workflowService.flagReport(id);
        return ResponseEntity.ok("Report flagged for misinformation.");
    }

    // URL becomes: /api/workflow/flagged
    @GetMapping("/flagged")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getFlaggedReports() {
        return ResponseEntity.ok(workflowService.getFlaggedReports());
    }
}
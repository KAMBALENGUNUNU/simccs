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
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Workflow", description = "Report review and workflow management endpoints")

public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/reports/{id}/review")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
        @Operation(
        summary = "Review crisis report",
        description = "Submit a review for a crisis report. Accessible to EDITOR and ADMIN roles.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<?> reviewReport(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        String editorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        workflowService.processReview(id, request, editorEmail);
        
        return ResponseEntity.ok("Report processed successfully with status: " + request.getAction());
    }
}
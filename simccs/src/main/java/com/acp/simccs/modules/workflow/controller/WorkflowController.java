package com.acp.simccs.modules.workflow.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.workflow.dto.ReviewRequest;
import com.acp.simccs.modules.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "Workflow", description = "Report review and workflow management endpoints")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/reports/{id}/status")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    @Operation(summary = "Change report status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        String editorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        workflowService.processReview(id, request, editorEmail);

        // Return type is inferred correctly by ResponseEntity<?>
        return ResponseDTO.<String>success("Status changed to: " + request.getAction(), null).toResponseEntity();
    }

    @GetMapping("/reports/{id}/versions")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersions(@PathVariable Long id) {
        // This will now handle List<Version> without invariance errors
        return ResponseDTO.success(workflowService.getVersions(id)).toResponseEntity();
    }

    @GetMapping("/reports/{id}/versions/{vId}")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getVersionDetail(@PathVariable Long id, @PathVariable Long vId) {
        return ResponseDTO.success(workflowService.getVersion(vId)).toResponseEntity();
    }

    @PostMapping("/reports/{id}/flag")
    public ResponseEntity<?> flagReport(@PathVariable Long id) {
        workflowService.flagReport(id);

        return ResponseDTO.<String>success("Report flagged for misinformation.", null).toResponseEntity();
    }

    @GetMapping("/flagged")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getFlaggedReports() {
        return ResponseDTO.success(workflowService.getFlaggedReports()).toResponseEntity();
    }
}
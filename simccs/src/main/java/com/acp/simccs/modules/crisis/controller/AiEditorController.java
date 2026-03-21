package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.modules.crisis.dto.AiEditorRequest;
import com.acp.simccs.modules.crisis.dto.AiEditorResponse;
import com.acp.simccs.modules.crisis.service.AiEditorService;
import com.acp.simccs.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crisis-reports")
@RequiredArgsConstructor
public class AiEditorController {

    private final AiEditorService aiEditorService;

    @PostMapping("/ai-edit")
    @PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN') or hasRole('JOURNALIST')")
    public ResponseEntity<ResponseDTO<AiEditorResponse>> getSuggestedEdits(@RequestBody AiEditorRequest request) {
        if (request.getReportId() == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("Report ID is required"));
        }
        AiEditorResponse response = aiEditorService.getSuggestedEdits(request);
        return ResponseEntity.ok(ResponseDTO.success("AI suggestions generated.", response));
    }
}

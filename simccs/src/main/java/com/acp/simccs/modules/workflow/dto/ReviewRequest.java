package com.acp.simccs.modules.workflow.dto;

import com.acp.simccs.modules.workflow.model.EWorkflowAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull
    private EWorkflowAction action; // APPROVE, REJECT, REQUEST_REVISION
    
    private String comment; // "Please verify the casualty count"
}
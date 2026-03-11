package com.acp.simccs.modules.crisis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiEditorRequest {
    private Long reportId;
    private String tone;
    private String customRules;
}

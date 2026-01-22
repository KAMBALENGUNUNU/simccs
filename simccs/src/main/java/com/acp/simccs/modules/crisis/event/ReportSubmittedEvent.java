package com.acp.simccs.modules.crisis.event;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSubmittedEvent {
    private CrisisReport report;
}
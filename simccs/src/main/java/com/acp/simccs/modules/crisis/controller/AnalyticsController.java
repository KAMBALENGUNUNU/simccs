package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Crisis analytics")
public class AnalyticsController {

    private final CrisisReportRepository crisisReportRepository;

    @GetMapping("/dashboard")
    // FIX: Changed generics from <? extends List<?>> to <Map<String, Object>>
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalReports = crisisReportRepository.count();

        stats.put("totalReports", totalReports);
        stats.put("activeRegions", 5);
        stats.put("totalCasualties", 120);

        return ResponseDTO.success(stats).toResponseEntity();
    }
}
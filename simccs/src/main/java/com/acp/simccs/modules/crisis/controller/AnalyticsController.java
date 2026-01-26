package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Crisis analytics")
public class AnalyticsController {

    @Autowired
    CrisisReportRepository crisisReportRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalReports = crisisReportRepository.count();
        // Simplified casualty count aggregation - ideally use a custom query
        // long totalCasualties = crisisReportRepository.sumCasualties(); 
        
        stats.put("totalReports", totalReports);
        stats.put("activeRegions", 5); // Mock data
        stats.put("totalCasualties", 120); // Mock data
        
        return ResponseEntity.ok(stats);
    }
}

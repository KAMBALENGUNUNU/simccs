package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Crisis analytics")
public class AnalyticsController {

    private final CrisisReportRepository crisisReportRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getDashboardStats() {
        List<CrisisReport> allReports = crisisReportRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        // 1. Basic Counters
        stats.put("totalReports", allReports.size());
        stats.put("totalCasualties", allReports.stream().mapToInt(r -> r.getCasualtyCount() == null ? 0 : r.getCasualtyCount()).sum());

        // 2. Count by Status (Pending, Approved, etc)
        stats.put("pendingReports", allReports.stream().filter(r -> "SUBMITTED".equals(r.getStatus().name()) || "DRAFT".equals(r.getStatus().name())).count());
        stats.put("approvedReports", allReports.stream().filter(r -> "VERIFIED".equals(r.getStatus().name())).count());
        stats.put("rejectedReports", allReports.stream().filter(r -> "REJECTED".equals(r.getStatus().name())).count());
        stats.put("flaggedReports", 0); // Logic for flagged if needed

        // 3. Reports by Status Map
        Map<String, Long> reportsByStatus = allReports.stream()
                .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting()));
        stats.put("reportsByStatus", reportsByStatus);

        // 4. Recent Activity (Last 5 reports)
        List<Map<String, Object>> recentActivity = allReports.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(5)
                .map(r -> {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("id", r.getId());
                    activity.put("title", r.getTitle());
                    activity.put("status", r.getStatus().name());
                    activity.put("createdAt", r.getCreatedAt());
                    return activity;
                })
                .collect(Collectors.toList());
        stats.put("recentActivity", recentActivity);

        return ResponseDTO.success(stats).toResponseEntity();
    }
}
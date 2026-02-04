package com.acp.simccs.modules.crisis.service;

import com.acp.simccs.modules.crisis.dto.ReportRequest;
import com.acp.simccs.modules.crisis.dto.ReportResponse;
import com.acp.simccs.modules.crisis.event.ReportSubmittedEvent;
import com.acp.simccs.modules.crisis.model.Category;
import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import com.acp.simccs.modules.crisis.repository.CategoryRepository;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.security.SecurityService; // <--- IMPORTANT IMPORT
import com.acp.simccs.modules.workflow.service.MisinformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private CrisisReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private MisinformationService misinformationService;

    @Transactional
    public ReportResponse createReport(ReportRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CrisisReport report = new CrisisReport();
        report.setAuthor(author);
        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setLocationLat(request.getLatitude());
        report.setLocationLng(request.getLongitude());
        report.setCasualtyCount(request.getCasualtyCount());
        report.setStatus(EReportStatus.SUBMITTED);

        // Encrypt
        report.setContentEncrypted(securityService.encrypt(request.getContent()));

        Set<Category> categories = new HashSet<>();
        if (request.getCategories() != null) {
            for (String catName : request.getCategories()) {
                Category category = categoryRepository.findByName(catName)
                        .orElseGet(() -> categoryRepository.save(new Category(catName)));
                categories.add(category);
            }
        }
        report.setCategories(categories);

        CrisisReport savedReport = reportRepository.save(report);

        // AI Scan & Event
        misinformationService.autoScanReport(savedReport);
        eventPublisher.publishEvent(new ReportSubmittedEvent(savedReport));

        return mapToResponse(savedReport, true);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(r -> mapToResponse(r, false)) // List view: don't decrypt body
                .collect(Collectors.toList());
    }

    public ReportResponse getReportById(Long id) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToResponse(report, true); // Detail view: decrypt body
    }

    @Transactional
    public ReportResponse updateReport(Long id, ReportRequest request, String userEmail) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setLocationLat(request.getLatitude());
        report.setLocationLng(request.getLongitude());
        report.setCasualtyCount(request.getCasualtyCount());

        report.setContentEncrypted(securityService.encrypt(request.getContent()));

        return mapToResponse(reportRepository.save(report), true);
    }

    @Transactional
    public void softDeleteReport(Long id) {
        CrisisReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(EReportStatus.DELETED);
        reportRepository.save(report);
    }

    private ReportResponse mapToResponse(CrisisReport report, boolean decryptContent) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setTitle(report.getTitle());
        response.setSummary(report.getSummary());
        response.setAuthorName(report.getAuthor().getFullName());
        response.setStatus(report.getStatus().name());
        response.setLatitude(report.getLocationLat());
        response.setLongitude(report.getLocationLng());
        response.setCasualtyCount(report.getCasualtyCount());
        response.setCreatedAt(report.getCreatedAt());

        if (decryptContent) {
            response.setContent(securityService.decrypt(report.getContentEncrypted()));
        } else {
            response.setContent("Encrypted content... view details to read.");
        }

        Set<String> catNames = report.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
        response.setCategories(catNames);

        return response;
    }
}
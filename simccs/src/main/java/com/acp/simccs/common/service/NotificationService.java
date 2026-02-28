package com.acp.simccs.common.service;

import com.acp.simccs.common.util.EmailTemplateUtil;
import com.acp.simccs.modules.identity.service.EmailService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userEmail) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout
        userEmitters.computeIfAbsent(userEmail, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userEmail, emitter));
        emitter.onTimeout(() -> removeEmitter(userEmail, emitter));
        emitter.onError((e) -> removeEmitter(userEmail, emitter));

        return emitter;
    }

    private void removeEmitter(String userEmail, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userEmail);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userEmail);
            }
        }
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.admin-email}")
    private String adminEmail;

    // 1. User Registration / Password
    public void sendPasswordReset(String to, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        String html = EmailTemplateUtil.getBasicTemplate("Password Reset",
                "You requested a password reset. Click below to proceed.", link, "Reset Password");
        emailService.sendHtmlMessage(to, "Reset Your Password", html);
    }

    public void notifyAdminOfNewUser(String userEmail, String userName) {
        String link = baseUrl + "/admin/dashboard";
        String html = EmailTemplateUtil.getBasicTemplate("New User Registration",
                "User " + userName + " (" + userEmail + ") has registered and is awaiting approval.",
                link, "Go to Admin Panel");
        emailService.sendHtmlMessage(adminEmail, "Action Required: New User", html);
    }

    public void notifyUserAccountApproved(String to) {
        String link = baseUrl + "/login";
        String html = EmailTemplateUtil.getBasicTemplate("Account Approved",
                "Your account has been approved by an administrator. You may now login.",
                link, "Login Now");
        emailService.sendHtmlMessage(to, "Welcome to SIMCCS", html);
    }

    // 2. Report Workflow
    public void notifyReportStatusChange(String to, Long reportId, String newStatus) {
        // Email Notification
        String link = baseUrl + "/reports/" + reportId;
        String html = EmailTemplateUtil.getBasicTemplate("Report Status Update",
                "Your report (ID: " + reportId + ") has been moved to status: <b>" + newStatus + "</b>",
                link, "View Report");
        emailService.sendHtmlMessage(to, "Update on Report #" + reportId, html);

        // Real-time SSE Notification
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(to);
        if (emitters != null) {
            String payload = "Report #" + reportId + " is now " + newStatus;
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(payload));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    removeEmitter(to, emitter);
                }
            }
        }
    }

    public void broadcastNotification(String payload) {
        for (CopyOnWriteArrayList<SseEmitter> emitters : userEmitters.values()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(payload));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    // Safe removal will happen via cleanup callbacks
                }
            }
        }
    }

    // 3. System Alerts
    public void sendSystemAlert(String processName, String errorMsg) {
        String html = EmailTemplateUtil.getAlertTemplate("Process: " + processName + "\nError: " + errorMsg);
        emailService.sendHtmlMessage(adminEmail, "CRITICAL: System Failure - " + processName, html);
    }
}
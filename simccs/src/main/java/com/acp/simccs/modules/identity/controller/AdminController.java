package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.modules.identity.model.AuditLog;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.AuditLogRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administration endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuditLogRepository auditLogRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) Boolean is_enabled) {
        if (is_enabled != null) {
            // Assuming we'd implement a custom finder, but for now filtering all users is generic fallback or we add method to repo
            // For efficiency, better to use repo method. Let's assume findAll for now or filter in memory if small, 
            // but let's assume we can rely on standard JpaRepository or add method later.
            // Let's filter stream for now to be safe without changing repo interface repeatedly.
            List<User> users = userRepository.findAll().stream()
                    .filter(u -> u.getIsEnabled().equals(is_enabled))
                    .toList();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<Object> approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(true);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog("APPROVE_USER", "ADMIN", "Approved user " + user.getEmail()));
        return ResponseEntity.ok("User approved.");
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<Object> banUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(false);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog("BAN_USER", "ADMIN", "Banned user " + user.getEmail()));
        return ResponseEntity.ok("User banned.");
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }
}

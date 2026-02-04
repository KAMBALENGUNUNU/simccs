package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.identity.model.AuditLog;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.AuditLogRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administration endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/users")
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsers(@RequestParam(required = false) Boolean is_enabled) {
        List<User> users;
        if (is_enabled != null) {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getIsEnabled().equals(is_enabled))
                    .toList();
        } else {
            users = userRepository.findAll();
        }
        return ResponseDTO.success(users).toResponseEntity();
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<ResponseDTO<Object>> approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(true);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog("APPROVE_USER", "ADMIN", "Approved user " + user.getEmail()));
        return ResponseDTO.success("User approved", null).toResponseEntity();
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ResponseDTO<Object>> banUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(false);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog("BAN_USER", "ADMIN", "Banned user " + user.getEmail()));
        return ResponseDTO.success("User banned", null).toResponseEntity();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ResponseDTO<List<AuditLog>>> getAuditLogs() {
        return ResponseDTO.success(auditLogRepository.findAll()).toResponseEntity();
    }
}
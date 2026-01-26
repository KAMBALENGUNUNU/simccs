package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.modules.identity.dto.ForgotPasswordRequest;
import com.acp.simccs.modules.identity.dto.PasswordResetRequest;
import com.acp.simccs.modules.identity.model.PasswordResetToken;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.PasswordResetTokenRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.identity.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/password")
@Tag(name = "Password Reset", description = "Password recovery endpoints")
public class PasswordResetController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/forgot")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token; // In real app, front-end URL
        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request", 
                "To reset your password, click the link below:\n" + resetLink);

        return ResponseEntity.ok("Password reset email sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Error: Invalid token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.badRequest().body("Error: Token expired.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password successfully reset.");
    }
}

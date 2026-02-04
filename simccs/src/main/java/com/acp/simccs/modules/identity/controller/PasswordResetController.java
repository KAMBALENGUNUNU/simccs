package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.identity.dto.ForgotPasswordRequest;
import com.acp.simccs.modules.identity.dto.PasswordResetRequest;
import com.acp.simccs.modules.identity.model.PasswordResetToken;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.PasswordResetTokenRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.identity.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Password recovery endpoints")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    @PostMapping("/forgot")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request",
                "To reset your password, click the link below:\n" + resetLink);

        // FIX: Added .<String> type hint
        return ResponseDTO.<String>success("Password reset email sent.", null).toResponseEntity();
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Error: Invalid token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            // FIX: Added .<String> type hint
            return ResponseDTO.<String>error("Error: Token expired.").toResponseEntity();
        }

        User user = resetToken.getUser();
        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        // FIX: Added .<String> type hint
        return ResponseDTO.<String>success("Password successfully reset.", null).toResponseEntity();
    }
}
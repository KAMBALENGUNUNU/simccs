package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.identity.dto.MfaSetupResponse;
import com.acp.simccs.modules.identity.dto.MfaVerifyRequest;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/mfa")
@RequiredArgsConstructor
@Tag(name = "MFA", description = "Multi-Factor Authentication endpoints")
public class MfaController {

    private final UserRepository userRepository;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @GetMapping("/setup")
    @Operation(summary = "Setup MFA")
    public ResponseEntity<ResponseDTO<MfaSetupResponse>> setupMfa() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("SIMCCS", user.getEmail(), key);

        user.setMfaSecret(secret);
        userRepository.save(user);

        return ResponseDTO.success(new MfaSetupResponse(secret, qrCodeUrl)).toResponseEntity();
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify MFA")
    public ResponseEntity<ResponseDTO<String>> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (user.getMfaSecret() == null) {
            // FIX: Added type hint <String>
            return ResponseDTO.<String>error("MFA is not set up for this user.").toResponseEntity();
        }

        boolean isCodeValid = gAuth.authorize(user.getMfaSecret(), Integer.parseInt(request.getCode()));

        if (isCodeValid) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            // FIX: Added type hint <String> because data is null
            return ResponseDTO.<String>success("MFA enabled/verified successfully.", null).toResponseEntity();
        } else {
            // FIX: Added type hint <String>
            return ResponseDTO.<String>error("Invalid MFA code.").toResponseEntity();
        }
    }
}
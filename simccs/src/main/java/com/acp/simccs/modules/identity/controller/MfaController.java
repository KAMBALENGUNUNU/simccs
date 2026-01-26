package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.modules.identity.dto.MfaSetupResponse;
import com.acp.simccs.modules.identity.dto.MfaVerifyRequest;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/mfa")
@Tag(name = "MFA", description = "Multi-Factor Authentication endpoints")
public class MfaController {

    @Autowired
    UserRepository userRepository;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Operation(summary = "Setup MFA", responses = {
            @ApiResponse(responseCode = "200", description = "MFA Setup Data", content = @Content(schema = @Schema(implementation = MfaSetupResponse.class)))
    })
    @GetMapping("/setup")
    public ResponseEntity<Object> setupMfa() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("SIMCCS", user.getEmail(), key);

        user.setMfaSecret(secret);
        userRepository.save(user);

        return ResponseEntity.ok(new MfaSetupResponse(secret, qrCodeUrl));
    }

    @Operation(summary = "Verify MFA", responses = {
            @ApiResponse(responseCode = "200", description = "MFA enabled/verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid MFA code")
    })
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body("MFA is not set up for this user.");
        }

        boolean isCodeValid = gAuth.authorize(user.getMfaSecret(), Integer.parseInt(request.getCode()));

        if (isCodeValid) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("MFA enabled/verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid MFA code.");
        }
    }
}

package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.identity.dto.JwtResponse;
import com.acp.simccs.modules.identity.dto.MfaSetupResponse;
import com.acp.simccs.modules.identity.dto.MfaVerifyRequest;
import com.acp.simccs.modules.identity.model.RefreshToken;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.identity.service.MfaService;
import com.acp.simccs.modules.identity.service.RefreshTokenService;
import com.acp.simccs.security.JwtService;
import com.acp.simccs.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/mfa")
@RequiredArgsConstructor
@Tag(name = "MFA", description = "Multi-Factor Authentication endpoints")
public class MfaController {

    private final MfaService mfaService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/setup")
    @Operation(summary = "Setup MFA")
    public ResponseEntity<ResponseDTO<MfaSetupResponse>> setupMfa() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return ResponseDTO.<MfaSetupResponse>error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")
                    .toResponseEntity();
        }

        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isMfaEnabled()) {
            return ResponseDTO.<MfaSetupResponse>error(HttpStatus.BAD_REQUEST.value(), "MFA is already enabled")
                    .toResponseEntity();
        }

        String secret = mfaService.generateSecretKey();
        user.setMfaSecret(secret);
        userRepository.save(user);

        String qrCodeUrl = mfaService.getQrCodeUrl(user.getEmail(), secret);

        MfaSetupResponse response = new MfaSetupResponse(secret, qrCodeUrl);
        return ResponseDTO.success("MFA Setup initialized", response).toResponseEntity();
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify MFA Code")
    public ResponseEntity<?> verifyMfa(
            @Valid @RequestBody MfaVerifyRequest request,
            @CookieValue(name = "mfa_session", required = false) String mfaSession) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        boolean isLoginFlow = false;

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            // Setup Flow (User is currently fully authenticated)
            username = authentication.getName();
        } else if (mfaSession != null && jwtService.validateMfaToken(mfaSession)) {
            // Login Flow (User has a valid mfa_session cookie)
            username = jwtService.getUserNameFromJwtToken(mfaSession);
            isLoginFlow = true;
        } else {
            return ResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized or missing MFA session")
                    .toResponseEntity();
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            int code = Integer.parseInt(request.getCode());
            if (!mfaService.verifyCode(user.getMfaSecret(), code)) {
                return ResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "Invalid MFA code").toResponseEntity();
            }
        } catch (NumberFormatException ex) {
            return ResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "Invalid code format").toResponseEntity();
        }

        if (!user.isMfaEnabled()) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            return ResponseDTO.success("MFA Setup completed successfully", "Success").toResponseEntity();
        }

        if (isLoginFlow) {
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());

            String jwt = jwtService.generateTokenFromUsername(userDetails.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                    userDetails.getEmail(), userDetails.isEnabled(), roles);

            ResponseCookie clearCookie = ResponseCookie.from("mfa_session", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/auth/mfa/verify")
                    .maxAge(0) // delete cookie
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                    .body(ResponseDTO.success("Login Successful", jwtResponse));
        }

        return ResponseDTO.success("MFA verified", "Success").toResponseEntity();
    }
}
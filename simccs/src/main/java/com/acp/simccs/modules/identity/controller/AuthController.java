package com.acp.simccs.modules.identity.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.common.service.NotificationService;
import com.acp.simccs.modules.identity.dto.*;
import com.acp.simccs.modules.identity.model.*;
import com.acp.simccs.modules.identity.repository.RoleRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.security.JwtService;
import com.acp.simccs.security.UserDetailsImpl;
import com.acp.simccs.modules.identity.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final NotificationService notificationService;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ResponseDTO<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!userDetails.isEnabled()) {
            return ResponseDTO.<JwtResponse>error(HttpStatus.FORBIDDEN.value(), "Error: Account is not approved by Admin yet.")
                    .toResponseEntity();
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        JwtResponse response = new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getEmail(), userDetails.isEnabled(), roles);

        return ResponseDTO.success("Login Successful", response).toResponseEntity();
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Refresh token")
    public ResponseEntity<ResponseDTO<TokenRefreshResponse>> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateTokenFromUsername(user.getEmail());
                    return ResponseDTO.success("Token refreshed", new TokenRefreshResponse(token, requestRefreshToken))
                            .toResponseEntity();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public ResponseEntity<ResponseDTO<MessageResponse>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseDTO.<MessageResponse>error("Error: Email is already in use!")
                    .toResponseEntity();
        }

        User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getFullName());
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_JOURNALIST)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    // FIX: Matching standard Spring Security naming convention used by frontend Enums
                    case "ROLE_ADMIN" -> roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow());
                    case "ROLE_EDITOR" -> roles.add(roleRepository.findByName(ERole.ROLE_EDITOR).orElseThrow());
                    default -> roles.add(roleRepository.findByName(ERole.ROLE_JOURNALIST).orElseThrow());
                }
            });
        }

        user.setRoles(roles);
        user.setIsEnabled(false);
        userRepository.save(user);

        // --- NOTIFICATION ---
        notificationService.notifyAdminOfNewUser(user.getEmail(), user.getFullName());

        return ResponseDTO.success("User registered successfully! Wait for Admin approval.", new MessageResponse("Registered"))
                .toResponseEntity();
    }
}
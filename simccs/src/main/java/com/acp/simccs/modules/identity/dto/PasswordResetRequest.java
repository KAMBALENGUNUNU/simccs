package com.acp.simccs.modules.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank
    private String token;
    
    @NotBlank
    private String newPassword;
}

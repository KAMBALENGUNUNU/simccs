package com.acp.simccs.modules.identity.dto;

import lombok.Data;
import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String email;
    private List<String> roles;
    private boolean isEnabled;

    public JwtResponse(String accessToken, String refreshToken, Long id, String email, boolean isEnabled, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.isEnabled = isEnabled;
        this.roles = roles;
    }
}
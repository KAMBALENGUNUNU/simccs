package com.acp.simccs.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI simccsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SIMCCS API")
                .description("Secure Information Management and Crisis Communication System API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("ACP Developer")
                    .email("dev@acp.com"))
                .license(new License()
                    .name("Proprietary License")
                    .url("https://acp.com/license")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new io.swagger.v3.oas.models.security.SecurityScheme()
                        .name("Bearer Authentication")
                        .type(Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
            .group("Authentication")
            .pathsToMatch("/api/auth/**")
            .build();
    }

    @Bean
    public GroupedOpenApi crisisApi() {
        return GroupedOpenApi.builder()
            .group("Crisis Management")
            .pathsToMatch("/api/reports/**", "/api/media/**", "/api/sync/**", "/api/analytics/**")
            .build();
    }

    @Bean
    public GroupedOpenApi workflowApi() {
        return GroupedOpenApi.builder()
            .group("Workflow")
            .pathsToMatch("/api/workflow/**")
            .build();
    }

    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
            .group("Communication")
            .pathsToMatch("/api/chat/**", "/ws/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("Admin & System")
            .pathsToMatch("/api/admin/**")
            .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("All APIs")
            .pathsToMatch("/api/**")
            .build();
    }
}
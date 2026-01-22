package com.acp.simccs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        // Define Security Scheme (JWT)
        String securitySchemeName = "Bearer Authentication";
        
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name(securitySchemeName);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        // Define Info
        Info info = new Info()
                .title("SIMCCS API")
                .version("1.0")
                .description("Secure Information Management and Crisis Communication System API")
                .contact(new Contact().name("ACP Developer").email("dev@acp.com"))
                .license(new License().name("Proprietary"));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme))
                .info(info);
    }
}
package com.project1.JavaCafe;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // A Spring Configuration class
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth"; // Use a consistent name

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. DEFINE the security scheme
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, // The name you're defining
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)   // HTTP authentication method
                                        .scheme("bearer")                 // The scheme is 'Bearer'
                                        .bearerFormat("JWT")              // The format is JSON Web Token
                        )
                )
                // 2. APPLY the defined security scheme globally
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}

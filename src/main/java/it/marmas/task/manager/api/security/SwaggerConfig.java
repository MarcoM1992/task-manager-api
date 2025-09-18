package it.marmas.task.manager.api.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
 
@Configuration
public class SwaggerConfig {

    /**
     * Configures Swagger/OpenAPI documentation for the API
     * - Adds API information (title, version, description)
     * - Configures JWT bearer authentication for protected endpoints
     */
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            // API metadata: title, version, description
            .info(new Info()
                .title("Task API")
                .version("1.0")
                .description("API protected by JWT"))
            
            // Add global security requirement: endpoints require bearer token
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            
            // Define security scheme for bearer JWT token
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("Authorization")      // Header name
                        .type(SecurityScheme.Type.HTTP) // HTTP authentication type
                        .scheme("bearer")          // Bearer token scheme
                        .bearerFormat("JWT")));    // JWT format
    }
}

package com.technogise.upgrad.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI definition.
     *
     * @return the OpenAPI bean
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Upgrad Learning Platform Backend")
                        .version("v1")
                        .description("API documentation for the Upgrad Learning Platform"));
    }
}

package com.pm.analyticsservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI analyticsServiceOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Analytics Service API")
            .description("API for analytics and events")
            .version("1.0.0"));
  }
}


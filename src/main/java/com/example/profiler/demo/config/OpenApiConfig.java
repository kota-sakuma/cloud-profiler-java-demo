package com.example.profiler.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Cloud Profiler Java Demo API")
                .description("This is a demo API for the Cloud Profiler Demo")
                .version("v0.1.0"));
  }
}

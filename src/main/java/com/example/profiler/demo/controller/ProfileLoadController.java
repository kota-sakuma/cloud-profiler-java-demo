package com.example.profiler.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Info", description = "Profile Load Controller")
public class ProfileLoadController {

  @GetMapping("/")
  @Operation(
      summary = "Information about services",
      description = "Returns a list of available APIs")
  public Map<String, Object> info() {
    return Map.of(
        "service", "cloud-profiler-java-demo",
        "docs", "/swagger-ui.html");
  }
}

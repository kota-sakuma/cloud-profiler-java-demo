package com.example.profiler.demo.controller;

import com.example.profiler.demo.service.CpuLoadService;
import com.example.profiler.demo.service.HeapLoadService;
import com.example.profiler.demo.service.MixedLoadService;
import com.example.profiler.demo.service.WallTimeLoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
    name = "Profile Load Controller",
    description = "This controller provides APIs to profile the load on the system")
public class ProfileLoadController {

  private final CpuLoadService cpuLoadService;
  private final HeapLoadService heapLoadService;
  private final MixedLoadService mixedLoadService;
  private final WallTimeLoadService wallTimeLoadService;

  public ProfileLoadController(
      CpuLoadService cpuLoadService,
      HeapLoadService heapLoadService,
      MixedLoadService mixedLoadService,
      WallTimeLoadService wallTimeLoadService) {
    this.cpuLoadService = cpuLoadService;
    this.heapLoadService = heapLoadService;
    this.mixedLoadService = mixedLoadService;
    this.wallTimeLoadService = wallTimeLoadService;
  }

  @GetMapping("/")
  @Operation(
      summary = "Information about services",
      description = "Returns a list of available APIs")
  public Map<String, Object> info() {
    return Map.of(
        "service", "cloud-profiler-java-demo",
        "docs", "/swagger-ui.html",
        "endpoints", List.of("/api/cpu", "/api/heap", "/api/mixed", "/api/wall-time"));
  }

  @GetMapping("/api/cpu")
  @Operation(summary = "Profile the CPU load", description = "Returns the CPU load profile")
  @ApiResponse(responseCode = "200", description = "Success")
  public Map<String, Object> cpu(
      @Parameter(
              description = "The number of iterations to profile the CPU load (between 1 and 20)",
              example = "5")
          @RequestParam(defaultValue = "5")
          int iterations) {
    int bounded = bound(iterations, 1, 20);
    long result = cpuLoadService.run(bounded);
    return Map.of("iterations", bounded, "result", result);
  }

  @GetMapping("/api/heap")
  @Operation(summary = "Profile the heap load", description = "Returns the heap load profile")
  public Map<String, Object> heap(
      @Parameter(
              description =
                  "The number of entries to profile the heap load (between 1000 and 100000)",
              example = "50000")
          @RequestParam(defaultValue = "50000")
          int entries) {
    int bounded = bound(entries, 1_000, 100_000);
    int cacheSize = heapLoadService.run(bounded);
    return Map.of("entries", bounded, "cacheSize", cacheSize);
  }

  @GetMapping("/api/wall-time")
  @Operation(
      summary = "Profile the wall time load",
      description = "Returns the wall time load profile")
  public Map<String, Object> wallTime(
      @Parameter(
              description = "Time to wait in milliseconds (between 100 and 5000)",
              example = "500")
          @RequestParam(defaultValue = "500")
          int delayMillis,
      @Parameter(
              description = "Number of threads conflicting with the main thread (between 1 and 16)",
              example = "4")
          @RequestParam(defaultValue = "4")
          int threads) {
    int boundedDelay = bound(delayMillis, 100, 5_000);
    int boundedThreads = bound(threads, 1, 16);
    long result = wallTimeLoadService.run(boundedDelay, boundedThreads);
    return Map.of("delayMillis", boundedDelay, "threads", boundedThreads, "result", result);
  }

  @GetMapping("/api/mixed")
  @Operation(summary = "Profile the mixed load", description = "Returns the mixed load profile")
  public Map<String, Object> mixed(
      @RequestParam(defaultValue = "3") int cpuIterations,
      @RequestParam(defaultValue = "10000") int heapEntries,
      @RequestParam(defaultValue = "300") int delayMillis) {
    return mixedLoadService.run(
        bound(cpuIterations, 1, 20),
        bound(heapEntries, 1_000, 100_000),
        bound(delayMillis, 100, 5_000));
  }

  private int bound(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}

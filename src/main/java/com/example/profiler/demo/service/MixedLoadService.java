package com.example.profiler.demo.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MixedLoadService {

  private final CpuLoadService cpuLoadService;
  private final HeapLoadService heapLoadService;
  private final WallTimeLoadService wallTimeLoadService;

  public MixedLoadService(
      CpuLoadService cpuLoadService,
      HeapLoadService heapLoadService,
      WallTimeLoadService wallTimeLoadService) {
    this.cpuLoadService = cpuLoadService;
    this.heapLoadService = heapLoadService;
    this.wallTimeLoadService = wallTimeLoadService;
  }

  public Map<String, Object> run(int cpuIterations, int heapEntries, int delayMillis) {
    long cpuResult = cpuLoadService.run(cpuIterations);
    int heapResult = heapLoadService.run(heapEntries);
    long wallTimeResult = wallTimeLoadService.run(delayMillis, 4);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("cpuResult", cpuResult);
    result.put("heapResult", heapResult);
    result.put("wallTimeResult", wallTimeResult);
    return result;
  }
}

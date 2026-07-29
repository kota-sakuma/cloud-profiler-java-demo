package com.example.profiler.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackgroundLoadScheduler {

  private static final Logger log = LoggerFactory.getLogger(BackgroundLoadScheduler.class);

  private final CpuLoadService cpuLoadService;
  private final HeapLoadService heapLoadService;

  public BackgroundLoadScheduler(CpuLoadService cpuLoadService, HeapLoadService heapLoadService) {
    this.cpuLoadService = cpuLoadService;
    this.heapLoadService = heapLoadService;
  }

  @Scheduled(fixedRate = 5_000)
  public void cpuBackgroundLoad() {
    long result = cpuLoadService.computePrimes(20_000);
    log.debug("Background CPU load completed: {}", result);
  }

  @Scheduled(fixedRate = 10_000)
  public void heapBackgroundLoad() {
    int size = heapLoadService.run(5_000);
    log.debug("Background heap load completed: cacheSize={}", size);
  }
}

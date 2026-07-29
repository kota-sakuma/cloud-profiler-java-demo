package com.example.profiler.demo.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;

@Service
public class WallTimeLoadService {

  private final Object lock = new Object();

  public long run(int delayMillis, int threads) {
    simulateIoLatency(delayMillis);
    return contendedLockWork(threads);
  }

  public void simulateIoLatency(int delayMillis) {
    fetchFromDatabase(delayMillis / 2);
    fetchFromRemoteService(delayMillis / 2);
  }

  private void fetchFromDatabase(int delayMillis) {
    sleep(delayMillis);
  }

  private void fetchFromRemoteService(int delayMillis) {
    sleep(delayMillis);
  }

  public long contendedLockWork(int threads) {
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch latch = new CountDownLatch(threads);
    long[] checksum = new long[1];

    for (int i = 0; i < threads; i++) {
      executor.submit(
          () -> {
            try {
              synchronized (lock) {
                sleep(50);
                checksum[0] += System.nanoTime();
              }
            } finally {
              latch.countDown();
            }
          });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      executor.shutdown();
    }
    return checksum[0];
  }

  private void sleep(int delayMillis) {
    try {
      Thread.sleep(delayMillis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

package com.example.profiler.demo.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class CpuLoadService {

  public long run(int iterations) {
    long result = 0;
    for (int i = 0; i < iterations; i++) {
      result += computePrimes(50_000);
      result += hashStrings(10_000);
      result += sortLargeArray(20_000);
    }
    return result;
  }

  public long computePrimes(int limit) {
    boolean[] isComposite = new boolean[limit + 1];
    long sum = 0;
    for (int i = 2; i <= limit; i++) {
      if (!isComposite[i]) {
        sum += i;
        for (int j = i * 2; j <= limit; j += i) {
          isComposite[j] = true;
        }
      }
    }
    return sum;
  }

  public long hashStrings(int iterations) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    long checksum = 0;
    for (int i = 0; i < iterations; i++) {
      byte[] hashed = digest.digest(("payload-" + i).getBytes());
      checksum += HexFormat.of().formatHex(hashed).hashCode();
    }
    return checksum;
  }

  public long sortLargeArray(int size) {
    int[] values = new int[size];
    for (int i = 0; i < size; i++) {
      values[i] = (i * 31 + 17) % size;
    }
    Arrays.sort(values);
    return values[0] + values[size - 1];
  }
}

package com.example.profiler.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class HeapLoadService {

  private final Map<String, List<String>> cache = new ConcurrentHashMap<>();

  public int run(int entries) {
    allocateStrings(entries, 128);
    buildObjectGraph(4, 10);
    populateCache(entries);
    return cache.size();
  }

  public List<String> allocateStrings(int count, int length) {
    List<String> strings = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      strings.add("item-" + i + "-" + "x".repeat(length));
    }
    cache.put("strings-" + count, strings);
    return strings;
  }

  public Node buildObjectGraph(int depth, int breadth) {
    Node root = new Node("root");
    buildLevel(root, depth, breadth);
    cache.put("graph-" + depth + "-" + breadth, List.of(root.label()));
    return root;
  }

  private void buildLevel(Node parent, int depth, int breadth) {
    if (depth == 0) return;
    for (int i = 0; i < breadth; i++) {
      Node child = new Node(parent.label() + "/" + i);
      parent.children().add(child);
      buildLevel(child, depth - 1, breadth);
    }
  }

  public void populateCache(int entries) {
    for (int i = 0; i < entries; i++) {
      cache.put("entry-" + i, List.of("value-" + i, "meta-" + i));
    }
  }

  public record Node(String label, List<Node> children) {
    Node(String label) {
      this(label, new ArrayList<>());
    }
  }
}

package client;

import java.util.ArrayList;
import java.util.List;

public class DataLogger {
  int successfulPosts;
  int failedPosts;
  List<String> outputLines;

  public DataLogger() {
    this.failedPosts = 0;
    this.successfulPosts = 0;
    this.outputLines = new ArrayList<>();
  }

  public synchronized void addSuccessfulPosts(int numPosts) {
    this.successfulPosts += numPosts;
  }

  public synchronized void addFailedPosts(int numPosts) {
    this.failedPosts += numPosts;
  }

  public synchronized void addOutputLines(List<String> lines) {
    this.outputLines.addAll(lines);
  }

  public int getSuccessfulPosts() {
    return this.successfulPosts;
  }

  public int getFailedPosts() {
    return this.failedPosts;
  }

  public List<String> getOutputLines() {
    return this.outputLines;
  }
}

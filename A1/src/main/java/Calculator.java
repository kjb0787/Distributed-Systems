import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
  private final double mean;
  private final double median;
  private final int throughput;
  private final int maxResponse;
  private final int percentile;
  private final List<Integer> latencies;

  public Calculator(List<String> posts, long wallTime, int successfulPosts) {
    this.latencies = posts.stream().map(p -> p.split(",")).map(rArr ->
            Integer.parseInt(rArr[2])).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList());
    this.mean = calculateMean();
    this.median = calculateMedian();
    this.throughput = (int) (successfulPosts / wallTime);
    this.maxResponse = latencies.get(latencies.size() - 1);
    this.percentile = latencies.get((int) Math.ceil(latencies.size() * 0.99) - 1);
  }

  private double calculateMean() {
    return this.latencies.stream().reduce(0, Integer::sum) / (double) latencies.size();
  }

  private double calculateMedian() {
    int size = latencies.size();
    return size % 2 != 0 ? (double) latencies.get(size / 2) :
            (latencies.get(size / 2) + latencies.get(size / 2 - 1)) / (double) 2;
  }

  public double getMean() {
    return this.mean;
  }

  public double getMedian() {
    return this.median;
  }

  public int getThroughput() {
    return this.throughput;
  }

  public int getMaxResponse() {
    return this.maxResponse;
  }

  public int getPercentile() {
    return this.percentile;
  }

  public void writeLog(DataLogger dataLogger) {
    try {
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("results.csv"));
      bufferedWriter.write("start_time,request_type,latency,response_code\n");
      for (String result : dataLogger.getOutputLines()) {
        bufferedWriter.write(result);
      }
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void printStats() {
    System.out.println("Average response time: " + this.getMean() + " ms.");
    System.out.println("Response time median: " + this.getMedian() + " ms.");
    System.out.println("99th percentile: " + this.getPercentile() + " ms.");
    System.out.println("Max response time: " + this.getMaxResponse() + " ms.");
    System.out.println("throughput: " + this.getThroughput() + " requests per second");
  }
}

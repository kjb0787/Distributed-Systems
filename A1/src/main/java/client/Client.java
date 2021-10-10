package client;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
  private static int numThreads, numLifts, numSkiers, numRuns;
  private static DataLogger dataLogger;
  // TODO: IP: 54.85.211.59, CHANGES each new start;
  private static String ipAddress = "3.90.207.173";

  private int request(int[] timeRange,
                      int[] skierIdRange,
                      int numPosts) throws InterruptedException {
    int successCount = 0;
    int failedCount = 0;
    // Create an instance of HttpClient.
    HttpClient client = new HttpClient();
    List<String> dataLog = new ArrayList<>();
    for (int i = numPosts; i > 0; --i) {
      int randomDay = getRandomNumberUsingNextInt(timeRange[0], timeRange[1]);
      int randomSkierId = getRandomNumberUsingNextInt(skierIdRange[0], skierIdRange[1]);

      String url = "http://" + ipAddress + ":8080/lab2/skiers/12/seasons/2019/day/" + randomDay +
              "/skier/" + randomSkierId;
      // Create a method instance
      PostMethod method = new PostMethod(url);
      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
              new DefaultHttpMethodRetryHandler(5, false));
      Timestamp postStartTime;
      Timestamp postEndTime;
      try {
        postStartTime = new Timestamp(System.currentTimeMillis());
        int statusCode = client.executeMethod(method);
        if (statusCode == HttpStatus.SC_CREATED) {
          ++successCount;
        } else {
          System.err.println("Method failed: " + method.getStatusLine());
          ++failedCount;
        }
        postEndTime = new Timestamp(System.currentTimeMillis());
        // Read the response body.
        byte[] responseBody = method.getResponseBody();
        long latency = postEndTime.getTime() - postStartTime.getTime();
        String fileLine = postStartTime + ",POST," + latency + "," + statusCode + "\n";
        dataLog.add(fileLine);
        // Use caution: ensure correct character encoding and is not binary data
        // TODO: System.out.println(new String(responseBody));
      } catch (HttpException e) {
        System.err.println("Fatal protocol violation: " + e.getMessage());
        e.printStackTrace();
      } catch (IOException e) {
        System.err.println("Fatal transport error: " + e.getMessage());
        e.printStackTrace();
      } finally {
        // Release the connection.
        method.releaseConnection();
      }
    }
    dataLogger.addSuccessfulPosts(successCount);
    dataLogger.addFailedPosts(failedCount);
    dataLogger.addOutputLines(dataLog);
    return successCount;
  }

  public static void phase(int numThreads,
                           int waitThreads,
                           String phaseName,
                           double phaseMultiplier,
                           CountDownLatch topLatch) throws InterruptedException {
    Client createClient = new Client();
    CountDownLatch completed = new CountDownLatch(waitThreads);
    long startTime = System.currentTimeMillis();
    System.out.println(phaseName + " start time: " + startTime);
    int proportion = numSkiers / numThreads;
    int requestsPerThread = (int) (numRuns * phaseMultiplier * proportion);
    System.out.println("number of requests in " + phaseName + " per thread: " + requestsPerThread);
    // should be 1 * 0.2 * 20000 = 4000
    System.out.println("number of total requests should be: " +
            (int) (numRuns * phaseMultiplier * numSkiers) + ", number of wait threads is: " +
            waitThreads);

    AtomicInteger sum = new AtomicInteger();
    for (int i = 0; i < numThreads; i++) {
      int finalI = i;
      Runnable thread = () -> {
        int count = 0;
        try {
          count = createClient.request(new int[]{1, 90},
                  new int[]{1 + finalI * proportion, (finalI + 1) * proportion},
                  requestsPerThread);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        sum.addAndGet(count);
        completed.countDown();
        topLatch.countDown();
      };
      new Thread(thread).start();
    }
    completed.await();
    System.out.println("number of successful posts (10%) in " + phaseName + ": " + sum);
    long endTime = System.currentTimeMillis();
    System.out.println(phaseName + " duration: " + (endTime - startTime) + " ms, " +
            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
  }

  public int getRandomNumberUsingNextInt(int min, int max) {
    Random random = new Random();
    return random.nextInt(max - min) + min;
  }

  public static void main(String[] args) throws InterruptedException {
    numThreads = Integer.parseInt(args[0]); // default: 32/64/128/256
    numSkiers = Integer.parseInt(args[1]); // default: 20000
    numLifts = Integer.parseInt(args[2]); // default: 40
    numRuns = Integer.parseInt(args[3]); // default: 1
    System.out.println("threads: " + numThreads + ", skiers: " + numSkiers + ", num lifts: " + numLifts);

    dataLogger = new DataLogger();
    int numThreadsDividedByFour = numThreads / 4;
    CountDownLatch topLatch = new CountDownLatch(numThreadsDividedByFour * 2 + numThreads);

    long startTime = System.currentTimeMillis();
    System.out.println("client start time: " + startTime);
    phase(numThreadsDividedByFour, (int) Math.ceil(numThreadsDividedByFour / 10.0), "phase one", 0.2, topLatch);
    phase(numThreads, (int) Math.ceil(numThreads / 10.0), "phase two", 0.6, topLatch);
    phase(numThreadsDividedByFour, numThreadsDividedByFour, "phase three", 0.1, topLatch);
    topLatch.await();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.println("whole program duration: " + duration + " ms, " +
            "in seconds: " + duration / 1000 + " s.");
    System.out.println("number of successful posts for the whole program: " + dataLogger.getSuccessfulPosts());
    System.out.println("number of failed posts for the whole program: " + dataLogger.getFailedPosts());
    System.out.println("program whole throughput: " + dataLogger.getSuccessfulPosts() / (duration / 1000) + " requests per second");

    /*
    Comment code below to run client part1
     */
    Calculator calculator = new Calculator(dataLogger.getOutputLines(), duration / 1000, dataLogger.getSuccessfulPosts());
    calculator.printStats();
    calculator.writeLog(dataLogger);
  }
}

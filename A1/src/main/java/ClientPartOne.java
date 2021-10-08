import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientPartOne {
  private static int numThreads, numLifts, numSkiers, numRuns;
  // TODO: IP: 54.85.211.59, CHANGES each new start;
  private static String ipAddress = "54.85.211.59";

  private int request(int[] timeRange,
                      int[] skierIdRange,
                      int numPosts) throws InterruptedException {
    int successCount = 0;
    // Create an instance of HttpClient.
    HttpClient client = new HttpClient();
    for (int i = numPosts; i > 0; --i) {
      int randomDay = getRandomNumberUsingNextInt(timeRange[0], timeRange[1]);
      int randomSkierId = getRandomNumberUsingNextInt(skierIdRange[0], skierIdRange[1]);

      String url = "http://" + ipAddress + ":8080/lab2/skiers/12/seasons/2019/day/" + randomDay +
              "/skier/" + randomSkierId;
      // Create a method instance.
      PostMethod method = new PostMethod(url);
      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
              new DefaultHttpMethodRetryHandler(5, false));
      try {
        int statusCode = client.executeMethod(method);
        if (statusCode == HttpStatus.SC_CREATED) {
          ++successCount;
        } else {
          System.err.println("Method failed: " + method.getStatusLine());
        }
        // Read the response body.
        byte[] responseBody = method.getResponseBody();
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
    return successCount;
  }

  public static void phase(int numThreads,
                           int waitThreads,
                           String phaseName,
                           double phaseMultiplier) throws InterruptedException {
    ClientPartOne createClient = new ClientPartOne();
    CountDownLatch completed = new CountDownLatch(waitThreads);
    long startTime = System.currentTimeMillis();
    System.out.println(phaseName + " start time: " + startTime);
    int proportion = numSkiers / numThreads;
    int requestsPerThread = (int) (numRuns * phaseMultiplier * proportion);
    System.out.println("number of requests in " + phaseName + " per thread: " + requestsPerThread);
    // should be 10 * 0.2 * 1024 = 2048
    System.out.println("number of total requests should be: " + (int) (numRuns * 0.2 * numSkiers));

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
      };
      new Thread(thread).start();
    }
    completed.await();
    System.out.println("number of successful posts in " + phaseName + ": " + sum);
    long endTime = System.currentTimeMillis();
    System.out.println(phaseName + "duration: " + (endTime - startTime) + " ms, " +
            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
  }

//  public static void phaseOne(int numThreadsDividedByFour,
//                              String ipAddress) throws InterruptedException {
//    ClientPartOne createClient = new ClientPartOne();
//    int waitThreads = (int) Math.ceil(numThreadsDividedByFour / 10.0);
//    CountDownLatch completed = new CountDownLatch(waitThreads);
//
//    long startTime = System.currentTimeMillis();
//    System.out.println("phase one start time: " + startTime);
//    int proportion = numSkiers / numThreadsDividedByFour;
//    AtomicInteger sum = new AtomicInteger();
//    System.out.println("number of requests in phase one per thread: " +
//            (int) (numRuns * 0.2 * proportion));
//    // should be 10 * 0.2 * 1024 = 2048
//    System.out.println("number of total requests: " + (int) (numRuns * 0.2 * numSkiers));
//
//    for (int i = 0; i < numThreadsDividedByFour; i++) {
//      int finalI = i;
//      Runnable thread = () -> {
//        int count = 0;
//        try {
//          count = createClient.request(ipAddress,
//                  new int[]{1, 90},
//                  new int[]{1 + finalI * proportion, (finalI + 1) * proportion},
//                  (int) (numRuns * 0.2 * proportion));
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        sum.addAndGet(count);
//        completed.countDown();
//      };
//      new Thread(thread).start();
//    }
//    completed.await();
//    System.out.println("number of successful posts in phase one: " + sum);
//    long endTime = System.currentTimeMillis();
//    System.out.println("phase one duration: " + (endTime - startTime) + " ms, " +
//            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
//  }
//
//  private static void phaseTwo(int numThreads,
//                               String ipAddress) throws InterruptedException {
//    ClientPartOne createClient = new ClientPartOne();
//    int waitThreads = (int) Math.ceil(numThreads / 10.0);
//    CountDownLatch completed = new CountDownLatch(waitThreads);
//    long startTime = System.currentTimeMillis();
//    System.out.println("phase two start time: " + startTime);
//    int proportion = numSkiers / numThreads;
//    AtomicInteger sum = new AtomicInteger();
//    for (int i = 0; i < numThreads; i++) {
//      int finalI = i;
//      Runnable thread = () -> {
//        int count = 0;
//        try {
//          count = createClient.request(ipAddress,
//                  new int[]{91, 360},
//                  new int[]{1 + finalI * proportion, (finalI + 1) * proportion},
//                  (int) (numRuns * 0.6 * proportion));
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        sum.addAndGet(count);
//        completed.countDown();
//      };
//      new Thread(thread).start();
//    }
//    completed.await();
//    System.out.println("number of successful posts in phase two: " + sum);
//    long endTime = System.currentTimeMillis();
//    System.out.println("phase two duration: " + (endTime - startTime) + " ms, " +
//            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
//  }
//
//  private static void phaseThree(int numThreadsDividedByFour,
//                                 String ipAddress) throws InterruptedException {
//    ClientPartOne createClient = new ClientPartOne();
//    CountDownLatch completed = new CountDownLatch(numThreadsDividedByFour);
//    long startTime = System.currentTimeMillis();
//    System.out.println("phase three start time: " + startTime);
//    int proportion = numSkiers / numThreadsDividedByFour;
//    AtomicInteger sum = new AtomicInteger();
//    for (int i = 0; i < numThreadsDividedByFour; i++) {
//      int finalI = i;
//      Runnable thread = () -> {
//        int count = 0;
//        try {
//          count = createClient.request(ipAddress,
//                  new int[]{91, 360},
//                  new int[]{1 + finalI * proportion, (finalI + 1) * proportion},
//                  (int) (numRuns * 0.1 * proportion));
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        sum.addAndGet(count);
//        completed.countDown();
//      };
//      new Thread(thread).start();
//    }
//    completed.await();
//    System.out.println("number of successful posts in phase three: " + sum);
//    long endTime = System.currentTimeMillis();
//    System.out.println("phase three duration: " + (endTime - startTime) + " ms, " +
//            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
//  }

  public int getRandomNumberUsingNextInt(int min, int max) {
    Random random = new Random();
    return random.nextInt(max - min) + min;
  }

  public static void main(String[] args) throws InterruptedException {
    numThreads = Integer.parseInt(args[0]); // default: 64
    numSkiers = Integer.parseInt(args[1]); // default: 1024
    numLifts = Integer.parseInt(args[2]); // default: 40
    numRuns = Integer.parseInt(args[3]); // default: 10

    long startTime = System.currentTimeMillis();
    System.out.println("client start time: " + startTime);
    int numThreadsDividedByFour = numThreads / 4;
    phase(numThreadsDividedByFour, (int) Math.ceil(numThreadsDividedByFour / 10.0), "phase one", 0.2);
    phase(numThreads, (int) Math.ceil(numThreads / 10.0), "phase two", 0.2);
    phase(numThreadsDividedByFour, numThreadsDividedByFour, "phase three", 0.1);
    long endTime = System.currentTimeMillis();
    System.out.println("whole program duration: " + (endTime - startTime) + " ms, " +
            "in seconds: " + (endTime - startTime) / Math.pow(10, 3) + " s.");
  }
}

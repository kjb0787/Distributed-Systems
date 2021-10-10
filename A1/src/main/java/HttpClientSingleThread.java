import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;

public class HttpClientSingleThread {

  static private int request(String ipAddress, int numPosts) {
    int successCount = 0;
    // Create an instance of HttpClient.
    HttpClient client = new HttpClient();
    for (int i = numPosts; i > 0; --i) {
      int randomDay = 1;
      int randomSkierId = 12;
      // TODO: IP: ec2-18-212-240-187.compute-1.amazonaws.com, CHANGES each new start;
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

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    System.out.println("single thread client start time: " + startTime);
    int requests = 1000;
    int res = request("3.90.207.173", requests);
    long endTime = System.currentTimeMillis();
    double endTimeSec = (endTime - startTime) / Math.pow(10, 3);
    System.out.println("whole program duration: " + (endTime - startTime) + " ms, " +
            "in seconds: " + endTimeSec + " s.");
    System.out.println("expected post number is: " + requests +
            ", while number of successful requests is: " + res);
    System.out.println("requests per second: " + res / endTimeSec);
  }
}

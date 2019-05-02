package com.optx.es;

import org.json.simple.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.parser.JSONParser;
import org.apache.http.impl.client.HttpClients;

public class EsRestClient {

  private static final EsRestClient esRestClient = new EsRestClient();
  private final JSONParser jsonParser = new JSONParser();
  public String jsonPayload;
  //public String url;
  //final EnvVars env = build.getEnvironment(listener);
  //String awsEsUrl = env.get("AWS_ES_URL");
  
  
  public JSONObject insertData(long jobPid, JSONObject jsonObject, String url) {

    jsonPayload = jsonObject.toString();
    // String url = "http://localhost:9200/accelq006/" + jobPid + "/?";
    // String url = "http://localhost:9200/accelq006/19415/?";
    // String url = "http://localhost:9200/accelq011/?";
    //url = System.getenv("AWS_ES_URL");
    System.out.println("Url: " + url);

    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader("content-type","application/json");
    if(jsonPayload != null && !jsonPayload.equals("")) {
      StringEntity requestEntity = new StringEntity(jsonPayload, org.apache.http.entity.ContentType.APPLICATION_JSON);
      httpPost.setEntity(requestEntity);
    }
    try {
      
      CloseableHttpClient httpClient = getHttpClient();
      CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

      BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      while((inputLine = reader.readLine()) != null) {
        response.append(inputLine);
      }
      reader.close();

      JSONObject jobInfo = (JSONObject) jsonParser.parse(response.toString());
      httpClient.close();

      return jobInfo;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private CloseableHttpClient getHttpClient() {
    CloseableHttpClient closeableHttpClient = HttpClients.custom().build();
    return closeableHttpClient;
  }
}

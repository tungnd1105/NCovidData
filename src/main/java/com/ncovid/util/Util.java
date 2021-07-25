package com.ncovid.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author ndtun
 * @package com.ncovid.util
 * @project NCovidData
 * @Date 25/07/2021
 */
public class Util {

  public static String date = "2021-04-27";
  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static LocalDate startDate = LocalDate.parse(date, formatter);
  public static LocalDate today = LocalDate.now();

  public static String urlDataByCurrent = "https://ncov.vncdc.gov.vn/v1/2/vietnam/by-current?start_time=2021-04-27" + "&end_time=" + today;

  public static String fetchDataJson(String url) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return httpResponse.body();
  }

}

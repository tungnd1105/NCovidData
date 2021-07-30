package com.ncovid.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  public static DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
  public static String timeUpdate = formatterDateTime.format(LocalDateTime.now());

  public static String urlDataByCurrent = "https://ncov.vncdc.gov.vn/v1/2/vietnam/by-current?start_time=2021-04-27" + "&end_time=" + today;
  public static String urlDataProvinceType = "https://ncov.vncdc.gov.vn/v1/2/vietnam/province-type?start_time=2021-04-27" + "&end_time=" + today;
  public static String urlDataAllProince = "https://tiemchungcovid19.gov.vn/api/province/public/all";
  public static String urlDataPopulationOfProince = "https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccine-allocate/province-detail";
  public static String urlDataVaccine = "https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-statistics/all";


  public static String fetchDataJson(String url) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return httpResponse.body();
  }

}

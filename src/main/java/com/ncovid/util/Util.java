package com.ncovid.util;

import com.ncovid.entity.APIData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

  private static final DecimalFormat df2 = new DecimalFormat("#.##");
  public static Logger logger = LoggerFactory.getLogger(Util.class);
  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
  public static String timeUpdate = formatterDateTime.format(LocalDateTime.now());
  public static String urlDataVaccinationSite = "https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-location/search?page=0&size=10&";
  public static File bodyGraphQl = new File("bodyGraphQl.json");

  public static String date = "2021-04-27";
  public static LocalDate startDate = LocalDate.parse(date, formatter);
  public static LocalDate today = LocalDate.now();

  public static String fetchDataJson(APIData api) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(api.getApi())).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return httpResponse.body();
  }

  public static String fetchDataJson(String api) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(api)).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    if (httpResponse.statusCode() == 500) {
      logger.error("internal server error sources data");
    }
    return httpResponse.body();
  }

  public static Iterable<CSVRecord> readerData(APIData api) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(api.getApi())).build();
    HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    StringReader stringReader = new StringReader(httpResponse.body());
    return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
  }


  public static String postMapping(APIData api, Path pathBody) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder()
      .uri(URI.create(api.getApi())).header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofFile(pathBody)).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return httpResponse.body();
  }

  public static String checkString(String string) {
    String result;
    if (string.trim().isEmpty() || string.matches("N/A")) {
      result = "0";
    } else {
      result = string.replace(".0", "").replaceAll("[^0-9]", "");
    }
    return result;
  }

  public static Double parseDouble(String string) {
    double result;
    if (string.trim().isEmpty()) {
      result = 0.0;
    } else {
      result = Double.parseDouble(string.replace(",", "."));
    }
    return result;
  }

  public static Double getPercent(int a, int b) {
    String result = df2.format((double) a * 100 / b);
    return Double.parseDouble(result);
  }
}

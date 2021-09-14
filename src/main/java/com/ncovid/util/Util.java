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
import java.text.DecimalFormat;

/**
 * @author ndtun
 * @package com.ncovid.util
 * @project NCovidData
 * @Date 25/07/2021
 */
public class Util {

  public static Logger logger = LoggerFactory.getLogger(Util.class);

  private static final DecimalFormat df2 = new DecimalFormat("#.##");

  public static String urlDataVaccinationSite = "https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-location/search?page=0&size=10&";

  public static File covidBydate = new File("JsonData/by-current.json");
  public static File dataCountry= new File("JsonData/dataCountry.json");
  public static File dataProvinceVN= new File("JsonData/dataProvinceVN.json");


  public static String validStatusCode(HttpResponse<String> httpResponse) {
    if (httpResponse.statusCode() == 500) {
      logger.error("500 Internal Server Error");
    } else if (httpResponse.statusCode() == 400) {
      logger.error("400 BadRequest");
    } else if (httpResponse.statusCode() == 403) {
      logger.error("400 forbidden access dined ");
    }
    return httpResponse.body();
  }

  public static String fetchDataJson(APIData api) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(api.getApi())).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return validStatusCode(httpResponse);
  }

  public static String fetchDataJson(String api) throws IOException, InterruptedException {
    HttpClient newClient = HttpClient.newHttpClient();
    HttpRequest newRequest = HttpRequest.newBuilder().uri(URI.create(api)).build();
    HttpResponse<String> httpResponse = newClient.send(newRequest, HttpResponse.BodyHandlers.ofString());
    return validStatusCode(httpResponse);
  }

  public static Iterable<CSVRecord> readerData(APIData api) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(api.getApi())).build();
    HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    StringReader stringReader = new StringReader(httpResponse.body());
    return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
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
    String result = df2.format((double) a/b * 100);
    return Double.parseDouble(result);
  }
}

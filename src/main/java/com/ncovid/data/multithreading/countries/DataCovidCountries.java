package com.ncovid.data.multithreading.countries;

import com.ncovid.entity.countries.Country;
import com.ncovid.entity.countries.CovidStatistics;
import com.ncovid.entity.countries.VaccinationStatistics;
import com.ncovid.repositories.countries.CountryRepositories;
import com.ncovid.repositories.countries.CovidStatisticsRepositories;
import com.ncovid.repositories.countries.VaccinationStatisticsRepositories;
import com.ncovid.util.AlphaCodeCountry;
import com.ncovid.util.Message;
import com.ncovid.util.Util;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.countries
 * @project NCovidData
 * @Date 04/08/2021
 */
@Service
public class DataCovidCountries {

  public static Logger logger = LoggerFactory.getLogger(DataCovidCountries.class);

  @Autowired
  private CountryRepositories countryRepositories;

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

  private void insertDataDetailCountry(String alphaCode) {
    try {
      Long startTime = System.currentTimeMillis();
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDetailCountry));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject jsonObject = (JSONObject) jsonArray.get(k);
        if (jsonObject.getString("alpha3Code").matches(alphaCode)) {
          Country country = new Country();
          country.setId(jsonObject.getString("alpha3Code"));
          country.setName(jsonObject.getString("name"));
          country.setAlpha2Code(jsonObject.getString("alpha2Code"));
          country.setCapital(jsonObject.getString("capital"));
          country.setRegion(jsonObject.getString("region"));
          country.setSubregion(jsonObject.getString("subregion"));
          country.setPopulation(jsonObject.getInt("population"));
          country.setNumericCode(jsonObject.get("numericCode").toString());
          JSONArray array = jsonObject.getJSONArray("latlng");
          countryRepositories.save(country);
        }
      }
      Long endTime = System.currentTimeMillis();
      logger.info("Thread-" + Thread.currentThread().getId() + Message.insertDataDetailCountry + (endTime - startTime) + " ms");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void insertCovidStatisticsData(String alphaCode) {
    try {
      Country country = countryRepositories.findById(alphaCode).orElse(null);
      Document document = Jsoup.connect(Util.urlDataCovidAllCountries).timeout(50000).get();
      Elements body = document.select("body");
      if (country != null) {
        for (Element element : body.select("tbody tr")) {
          if (element.select("td").get(1).text().matches(country.getName())) {
            CovidStatistics SCovid = new CovidStatistics();
            SCovid.setTotalCase(Util.parseInt(element.select("td").get(2).text()));
            SCovid.setNewCases(Util.parseInt(element.select("td").get(3).text()));
            SCovid.setTotalDeaths(Util.parseInt(element.select("td").get(4).text()));
            SCovid.setNewDeaths(Util.parseInt(element.select("td").get(5).text()));
            SCovid.setTotalRecovered(Util.parseInt(element.select("td").get(6).text()));
            SCovid.setNewRecovered(Util.parseInt(element.select("td").get(7).text()));
            SCovid.setActiveCases(Util.parseInt(element.select("td").get(8).text()));
            SCovid.setSeriousCritical(Util.parseInt(element.select("td").get(9).text()));
            SCovid.setTotalTest(Util.parseInt(element.select("td").get(10).text()));
            SCovid.setCountry(country);
            dataCovidRepositories.save(SCovid);
          }
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

//  private void insertVaccinationStatisticsData(String alphaCode) {
//    try {
//  //    Iterable<CSVRecord> data = Util.readerData(Util.urlDataVaccinationsAllCountries);
//      Country country = countryRepositories.findCountryById(alphaCode);
//      System.out.println(country);
////      for (CSVRecord record : data) {
////        if(record.get("iso_code").matches(alphaCode)) {
////          if(country != null) {
////            VaccinationStatistics dataVaccinations = new VaccinationStatistics();
////            dataVaccinations.setTotalVaccine(Util.parseInt(record.get("total_vaccinations")));
////            dataVaccinations.setNewVaccine(Util.parseInt(record.get("new_vaccinations")));
////            dataVaccinations.setTotalFullyInjected(Util.parseInt(record.get("people_fully_vaccinated")));
////            dataVaccinations.setTotalInjectedOneDose(Util.parseInt(record.get("people_vaccinated")));
////            dataVaccinations.setTotalVaccinePercent(Util.parseDouble(record.get("total_vaccinations_per_hundred")));
////            dataVaccinations.setFullyInjectedPercent(Util.parseDouble(record.get("people_fully_vaccinated_per_hundred")));
////            dataVaccinations.setInjectedOneDosePercent(Util.parseDouble(record.get("people_vaccinated_per_hundred")));
////            dataVaccinations.setCountry(country);
////            dataVaccinationRepositories.save(dataVaccinations);
////          }
////        }
////      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

  @EventListener(ApplicationReadyEvent.class)
  @Async("taskExecutor")
  public void runMultithreading() throws IOException, InterruptedException {
    List<String> alphaCodeList = AlphaCodeCountry.getAllAlphaCode();
    for (String alphaCode : alphaCodeList) {
      CompletableFuture.runAsync(() -> insertDataDetailCountry(alphaCode))
        .thenRun(() -> insertCovidStatisticsData(alphaCode));
    }
  }
}

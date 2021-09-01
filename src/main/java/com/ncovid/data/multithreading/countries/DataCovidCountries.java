package com.ncovid.data.multithreading.countries;

import com.ncovid.entity.APIData;
import com.ncovid.entity.countries.Country;
import com.ncovid.entity.countries.CovidStatistics;
import com.ncovid.entity.countries.VaccinationStatistics;
import com.ncovid.repositories.countries.CountryRepositories;
import com.ncovid.repositories.countries.CovidStatisticsRepositories;
import com.ncovid.repositories.countries.VaccinationStatisticsRepositories;
import com.ncovid.util.AlphaCodeCountry;
import com.ncovid.util.Message;
import com.ncovid.util.Util;
import com.ncovid.util.UtilDate;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.countries
 * @project NCovidData
 * @Date 04/08/2021
 * description class: insert new data covid 19 and Vaccination of countries
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

  private Country insertDataDetailCountry(String alphaCode) {
    Country country = new Country();
    try {
      JSONArray jsonArray = new JSONArray(new String(Files.readAllBytes(Paths.get(Util.dataCountry.getAbsolutePath()))));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject jsonObject = (JSONObject) jsonArray.get(k);
        if (jsonObject.getString("alpha3Code").matches(alphaCode)) {
          country.setId(jsonObject.getString("alpha3Code"));
          country.setName(jsonObject.getString("name"));
          country.setAlpha2Code(jsonObject.getString("alpha2Code"));
          country.setCapital(jsonObject.getString("capital"));
          country.setRegion(jsonObject.getString("region"));
          country.setSubregion(jsonObject.getString("subregion"));
          country.setPopulation(jsonObject.getInt("population"));
          country.setNumericCode(jsonObject.get("numericCode").toString());
          countryRepositories.save(country);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return country;
  }

  private Country insertVaccinationsStatisticsData(Country country) {
    VaccinationStatistics dataVaccinations = new VaccinationStatistics();
    try {
      Iterable<CSVRecord> data = Util.readerData(APIData.vaccinationsByCountry);
      if (country != null) {
        for(CSVRecord record: data){
          if (country.getId().matches(record.get("iso_code"))) {
            dataVaccinations.setTotalVaccine(Util.checkString(record.get("total_vaccinations")));
            dataVaccinations.setNewVaccine(Util.checkString(record.get("new_vaccinations")));
            dataVaccinations.setTotalFullyInjected(Util.checkString(record.get("people_fully_vaccinated")));
            dataVaccinations.setTotalInjectedOneDose(Util.checkString(record.get("people_vaccinated")));
            dataVaccinations.setTotalVaccinePercent(Util.parseDouble(record.get("total_vaccinations_per_hundred")));
            dataVaccinations.setFullyInjectedPercent(Util.parseDouble(record.get("people_fully_vaccinated_per_hundred")));
            dataVaccinations.setInjectedOneDosePercent(Util.parseDouble(record.get("people_vaccinated_per_hundred")));
          }
          dataVaccinations.setUpdateTime(UtilDate.timeUpdate);
          dataVaccinations.setCountry(country);
          dataVaccinationRepositories.save(dataVaccinations);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
     return dataVaccinations.getCountry();
  }

  private Country insertCovidStatisticsData(Country country) {
    CovidStatistics SCovid = new CovidStatistics();
    try {
      Document document = Jsoup.connect(APIData.covidByCountry.getApi()).timeout(500000).get();
      Elements body = document.select("body").select("div#nav-today table#main_table_countries_today");
      if (country != null) {
        for (Element element: body.select("tbody tr")){
          if (element.select("td").get(1).text().contains(country.getName())) {
            SCovid.setTotalCase(Util.checkString(element.select("td").get(2).text()));
            SCovid.setNewCases(Util.checkString(element.select("td").get(3).text()));
            SCovid.setTotalDeaths(Util.checkString(element.select("td").get(4).text()));
            SCovid.setNewDeaths(Util.checkString(element.select("td").get(5).text()));
            SCovid.setTotalRecovered(Util.checkString(element.select("td").get(6).text()));
            SCovid.setNewRecovered(Util.checkString(element.select("td").get(7).text()));
            SCovid.setActiveCases(Util.checkString(element.select("td").get(8).text()));
            SCovid.setSeriousCritical(Util.checkString(element.select("td").get(9).text()));
            SCovid.setTotalTest(Util.checkString(element.select("td").get(10).text()));
          }
          SCovid.setUpdateTime(UtilDate.timeUpdate);
          SCovid.setCountry(country);
          dataCovidRepositories.save(SCovid);
        }
        logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataCountry + country.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return SCovid.getCountry();
  }


  /**
   * if data not yet in databases run insert new
   * use multithreading to performance optimization
   * each threading will be insert data covid, vaccine of provinces  by province code
   * each threading flow task
   * insertDataDetailCountry -> insertVaccinationsStatisticsData ->  insertCovidStatisticsData
   */
  @EventListener(ApplicationReadyEvent.class)
  @Async("taskExecutor")
  public void runMultithreading() throws IOException, InterruptedException {
    List<Country> checkData = countryRepositories.findAll();
    if (checkData.size() == 0) {
      List<String> alphaCodeList = AlphaCodeCountry.getAllAlphaCode();
      for (String alphaCode : alphaCodeList) {
       CompletableFuture.supplyAsync(() ->insertDataDetailCountry(alphaCode))
         .thenApplyAsync(this::insertVaccinationsStatisticsData)
         .thenApplyAsync(this::insertCovidStatisticsData);
      }
    }
  }
}

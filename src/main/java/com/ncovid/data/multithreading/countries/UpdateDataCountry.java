package com.ncovid.data.multithreading.countries;

import com.ncovid.entity.APIData;
import com.ncovid.entity.countries.Country;
import com.ncovid.repositories.countries.CountryRepositories;
import com.ncovid.repositories.countries.CovidStatisticsRepositories;
import com.ncovid.repositories.countries.VaccinationStatisticsRepositories;
import com.ncovid.util.AlphaCodeCountry;
import com.ncovid.util.Message;
import com.ncovid.util.Util;
import com.ncovid.util.UtilDate;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.countries
 * @project NCovidData
 * @Date 07/08/2021
 * description class: update data covid, vaccine of all country
 */
@Service
public class UpdateDataCountry {

  public static Logger logger = LoggerFactory.getLogger(UpdateDataCountry.class);

  @Autowired
  private CountryRepositories countryRepositories;

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

  private void updateVaccinationData(Country country) throws IOException, InterruptedException {
    Iterable<CSVRecord> data = Util.readerData(APIData.vaccinationsByCountry);
    if (country != null) {
      data.forEach(record -> {
        if (country.getId().matches(record.get("iso_code"))) {
          country.getVaccinationData().setTotalVaccine(Util.checkString(record.get("total_vaccinations")));
          country.getVaccinationData().setNewVaccine(Util.checkString(record.get("new_vaccinations")));
          country.getVaccinationData().setUpdateTime(UtilDate.timeUpdate);
          country.getVaccinationData().setTotalFullyInjected(Util.checkString(record.get("people_fully_vaccinated")));
          country.getVaccinationData().setTotalInjectedOneDose(Util.checkString(record.get("people_vaccinated")));
          country.getVaccinationData().setTotalVaccinePercent(Util.parseDouble(record.get("total_vaccinations_per_hundred")));
          country.getVaccinationData().setFullyInjectedPercent(Util.parseDouble(record.get("people_fully_vaccinated_per_hundred")));
          country.getVaccinationData().setInjectedOneDosePercent(Util.parseDouble(record.get("people_vaccinated_per_hundred")));
          dataVaccinationRepositories.save(country.getVaccinationData());
        }
      });
    }
  }

  private void updateCovidData(Country country) throws IOException {
    Document document = Jsoup.connect(APIData.covidByCountry.getApi()).timeout(50000).get();
    Elements body = document.select("body").select("div#nav-today table#main_table_countries_today");
    if (country != null) {
      body.select("tbody tr").forEach(element -> {
        if (element.select("td").get(1).text().replaceAll("\\s+","")
          .equalsIgnoreCase(country.getName().replaceAll("\\s+",""))) {
          country.getCovidData().setTotalCase(Util.checkString(element.select("td").get(2).text()));
          country.getCovidData().setNewCases(Util.checkString(element.select("td").get(3).text()));
          country.getCovidData().setUpdateTime(UtilDate.timeUpdate);
          country.getCovidData().setTotalDeaths(Util.checkString(element.select("td").get(4).text()));
          country.getCovidData().setNewDeaths(Util.checkString(element.select("td").get(5).text()));
          country.getCovidData().setTotalRecovered(Util.checkString(element.select("td").get(6).text()));
          country.getCovidData().setNewRecovered(Util.checkString(element.select("td").get(7).text()));
          country.getCovidData().setActiveCases(Util.checkString(element.select("td").get(8).text()));
          country.getCovidData().setSeriousCritical(Util.checkString(element.select("td").get(9).text()));
          country.getCovidData().setTotalTest(Util.checkString(element.select("td").get(10).text()));
          dataCovidRepositories.save(country.getCovidData());
        }
      });
      logger.info("Threading-" + Thread.currentThread().getId() + Message.updateDataCountry + country.getName());
    }

  }


  /**
   * update data realtime
   * use multithreading to performance optimization
   * each threading will be flow task
   * updateVaccinationData -> updateCovidData
   * 0PM o'clock,6Am o'clock ,12AM o'clock,8PM o'clock everyday
   */
  @Async("taskExecutor")
  @Scheduled(cron = "0 7 11 * * * ")
  public void runMultithreading() throws IOException, InterruptedException {
    List<String> alphaCodeList = AlphaCodeCountry.getAllAlphaCode();
    List<Country> checkData = countryRepositories.findAll();
    if (checkData.size() != 0) {
      for (String alphaCode : alphaCodeList) {
        CompletableFuture<Country> completableFuture =
          CompletableFuture.supplyAsync(() -> countryRepositories.findById(alphaCode).orElse(null));

        completableFuture
          .thenRun(() -> {
            try {
              updateVaccinationData(completableFuture.get());
            } catch (InterruptedException | ExecutionException | IOException e) {
              e.printStackTrace();
            }
          })
          .thenRun(() -> {
            try {
              updateCovidData(completableFuture.get());
            } catch (IOException | InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          });
      }
    }
  }
}

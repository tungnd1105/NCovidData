package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.CovidStatistics;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.util.Message;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import com.ncovid.util.UtilDate;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.text.Collator;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * note: because cross-origin request source data, use data json  demo
 * description class: insert new data covid 19 and Vaccination  of all province/city in Vietnam
 */

@Service
public class DataCovid {

  public static Logger logger = LoggerFactory.getLogger(DataCovid.class);

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;


  private Province insertDataInfoOfProvince(Integer provinceCode) {
    Province province = new Province();
    try {
      JSONArray jsonDataProvinceArray = new JSONArray(new String(Files.readAllBytes(Paths.get(Util.dataProvinceVN.getAbsolutePath()))));
      for (int k = 0; k < jsonDataProvinceArray.length(); k++) {
        JSONObject object = (JSONObject) jsonDataProvinceArray.get(k);
        if (object.getInt("code") == provinceCode) {
          province.setProvinceCode(object.getInt("code"));
          province.setShortName(object.getString("shortName"));
          province.setName(object.getString("name"));
          province.setType(object.getString("division_type"));
          province.setPopOverEighteen(object.getInt("popOverEighteen"));
          province.setTotalPopulation(object.getInt("population"));
          province.setCountry("Vietnam");
          provinceRepositories.save(province);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }

  private Province insertCovidStatisticsData(Province province) throws IOException, InterruptedException {
    Collator usCollator = Collator.getInstance(Locale.US);
    usCollator.setStrength(Collator.PRIMARY);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(APIData.covidByProvince.getApi()));
    JSONArray jsonArray = jsonObject.getJSONArray("locations");
    CovidStatistics covidStatistics = new CovidStatistics();
    for (int k = 0; k < jsonArray.length(); k++) {
      JSONObject data = jsonArray.getJSONObject(k);
      if (province != null) {
        if (usCollator.compare(data.getString("name"), province.getShortName()) == 0 ||
          data.getString("name").contains(province.getShortName())) {
          covidStatistics.setCases(data.getInt("cases"));
          covidStatistics.setDeaths(data.getInt("death"));
          covidStatistics.setTreating(data.getInt("treating"));
          covidStatistics.setCasesPercent(Util.getPercent(covidStatistics.getCases(), province.getTotalPopulation()));
          covidStatistics.setDeathsPercent(Util.getPercent(covidStatistics.getDeaths(), province.getPopOverEighteen()));
        }
      }
      covidStatistics.setUpdateTime(UtilDate.timeUpdate);
      covidStatistics.setProvince(province);
      dataCovidRepositories.save(covidStatistics);
    }
    return provinceRepositories.findById(province.getProvinceCode()).orElse(null);
  }

  private Province insertDataNewCasesByDate(Province province) {
    try {
      JSONArray jsonArray = new JSONArray(new String(Files.readAllBytes(Paths.get(Util.covidBydate.getAbsolutePath()))));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject jsonObject = jsonArray.getJSONObject(k);
        if (jsonObject.getInt("ma") == province.getProvinceCode()) {
          JSONObject dataByDate = (JSONObject) jsonObject.get("data");
          for (LocalDate date = UtilDate.startDate; date.isBefore(LocalDate.parse("2021-09-13")); date = date.plusDays(1)) {
            DataHistory dataHistory = new DataHistory();
            dataHistory.setDate(date);
            dataHistory.setNewCases(dataByDate.getInt(date.toString()));
            dataHistory.setCovidData(province.getCovidData());
            dataHistoryRepositories.save(dataHistory);
          }
        }
      }
      logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataCovidProvince + province.getName());
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }


  @EventListener(ApplicationReadyEvent.class)
  @Async("taskExecutor")
  public void processingDataCovidVietnam() throws IOException {
    List<Province> dataExist = provinceRepositories.findAll();
    if (dataExist.size() == 0) {
      logger.info("starting assign task for threading by province code ");
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture.supplyAsync(() -> insertDataInfoOfProvince(provinceCode))
          .thenApplyAsync(province -> {
            try {
              province = insertCovidStatisticsData(province);
            } catch (IOException | InterruptedException e) {
              e.printStackTrace();
            }
            return province;
          }).thenApplyAsync(this::insertDataNewCasesByDate);
      }
    }
  }
}


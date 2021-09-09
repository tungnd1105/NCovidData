package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.APIData;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project VaccinationStatistics.java
 * @Date 09/09/2021
 */


@Service
public class UpdateDataCovid {

  public static Logger logger = LoggerFactory.getLogger(UpdateDataCovid.class);

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private CovidStatisticsRepositories covidStatisticsRepositories;


  private Province updateCovidStatisticsData(Integer provinceCode) throws IOException, InterruptedException {
    Province province = provinceRepositories.findById(provinceCode).orElse(null);
    if (province != null) {
      Path pathFile = Paths.get(Util.bodyGraphQl.getAbsolutePath());
      JSONObject jsonObject = new JSONObject(Util.postMapping(APIData.covidByProvince, pathFile));
      JSONObject jsonObject2 = jsonObject.getJSONObject("data");
      JSONArray jsonArray = jsonObject2.getJSONArray("provinces");
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject data = jsonArray.getJSONObject(k);
        String provinceId = data.getString("Province_Id").replaceAll("[^0-9]", "");
        if (province.getProvinceCode() == Integer.parseInt(provinceId)) {
          province.getCovidData().setUpdateTime(UtilDate.timeUpdate);
          province.getCovidData().setCases(data.getInt("Confirmed"));
          province.getCovidData().setDeaths(data.getInt("Deaths"));
          province.getCovidData().setRecovered(data.getInt("Recovered"));
          province.getCovidData().setCasesPercent(Util.getPercent(province.getCovidData().getCases(), province.getTotalPopulation()));
          province.getCovidData().setDeathsPercent(Util.getPercent(province.getCovidData().getDeaths(), province.getPopOverEighteen()));
          province.getCovidData().setRecoveredPercent(Util.getPercent(province.getCovidData().getRecovered(), province.getTotalPopulation()));
          covidStatisticsRepositories.save(province.getCovidData());
        }
      }
    }
    return province;
  }

  public Province updateDataNewCases(Province province) throws IOException, InterruptedException {
    Collator usCollator = Collator.getInstance(Locale.US);
    usCollator.setStrength(Collator.PRIMARY);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(APIData.newCasesByDate.getApi()));
    JSONArray jsonArray = jsonObject.getJSONArray("locations");
    for (int k = 0; k < jsonArray.length(); k++) {
      JSONObject data = jsonArray.getJSONObject(k);
      if (usCollator.compare(data.getString("name"), province.getShortName()) == 0 ||
         data.getString("name").contains(province.getShortName())) {
        province.getCovidData().getDataHistory().forEach(e -> {
          if (UtilDate.today.minusDays(1).isEqual(e.getDate())) {
            province.getCovidData().setYesterday(e.getNewCases());
          }
        });
        province.getCovidData().setToday(data.getInt("casesToday"));
        covidStatisticsRepositories.save(province.getCovidData());
        DataHistory dataHistory = dataHistoryRepositories.findByDate(province.getProvinceCode(), UtilDate.today);
        if (dataHistory != null) {
          dataHistory.setNewCases(data.getInt("casesToday"));
          dataHistoryRepositories.save(dataHistory);
        } else {
          DataHistory newCasesDate = new DataHistory();
          newCasesDate.setDate(UtilDate.today);
          newCasesDate.setNewCases(data.getInt("casesToday"));
          newCasesDate.setCovidData(province.getCovidData());
          dataHistoryRepositories.save(newCasesDate);
        }
      }
    }
    logger.info("Thread-" + Thread.currentThread().getId() + Message.updateDataCovidProvince + province.getName());
    return province;
  }

  // update everyday
  @Async("taskExecutor")
  @Scheduled(cron = "0 0 10 * * *")
  public void multithreading() throws IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.supplyAsync(() -> {
        Province province = new Province();
        try {
          province =  updateCovidStatisticsData(provinceCode);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
        return province;
      }).thenApplyAsync(province -> {
        try {
          return updateDataNewCases(province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
        return  province;
      });
    }
  }
}

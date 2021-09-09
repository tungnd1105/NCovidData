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
import java.text.Collator;
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


  public void updateDataCovid(Integer provinceCode) throws IOException, InterruptedException {
    Province province = provinceRepositories.findById(provinceCode).orElse(null);
    Collator usCollator = Collator.getInstance(Locale.US);
    usCollator.setStrength(Collator.PRIMARY);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(APIData.covidByProvince.getApi()));
    JSONArray jsonArray = jsonObject.getJSONArray("locations");
    if (province != null) {
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject data = jsonArray.getJSONObject(k);
        if (usCollator.compare(data.getString("name"), province.getShortName()) == 0 ||
          data.getString("name").contains(province.getShortName())) {

          province.getCovidData().setNewCases(data.getInt("casesToday"));

          if (province.getCovidData().getDeaths() != data.getInt("death")) {
            int newDeaths = data.getInt("death") - province.getCovidData().getDeaths();
            province.getCovidData().setNewDeaths(newDeaths);
          }

          province.getCovidData().getDataHistory().forEach(e -> {
            if (UtilDate.today.minusDays(1).isEqual(e.getDate())) {
              province.getCovidData().setYesterdayCases(e.getNewCases());
            }
          });

          covidStatisticsRepositories.save(province.getCovidData());
          // update new case by date
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
    }
  }

  // update everyday
  @Async("taskExecutor")
  @Scheduled(cron = "0 22 21 * * *")
  public void multithreading() throws IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.runAsync(() -> {
        try {
          updateDataCovid(provinceCode);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }
}

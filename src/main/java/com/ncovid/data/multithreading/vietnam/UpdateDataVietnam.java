package com.ncovid.data.multithreading.vietnam;


import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.VaccinationStatisticsRepositories;
import com.ncovid.util.Message;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import com.ncovid.util.UtilDate;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project CovidStatistics.java
 * @Date 27/08/2021
 */

@Service
public class UpdateDataVietnam {

  public static Logger logger = LoggerFactory.getLogger(UpdateDataVietnam.class);

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

  @Autowired
  private CovidStatisticsRepositories covidStatisticsRepositories;


  private Province updateVaccinationStatisticsData(Integer provinceCode) {
    Province province = provinceRepositories.findById(provinceCode).orElse(null);
    try {
      if (province != null) {
        JSONArray jsonArray = new JSONArray(Util.fetchDataJson(APIData.vaccinationsByProvince));
        for (int k = 0; k < jsonArray.length(); k++) {
          JSONObject object = (JSONObject) jsonArray.get(k);
          if (object.getInt("provinceCode") == province.getProvinceCode()) {
            province.getVaccinationData().setUpdateTime(UtilDate.timeUpdate);
            province.getVaccinationData().setTotalInjected(object.getInt("totalInjected"));
            province.getVaccinationData().setTotalInjectedOneDose(object.getInt("totalOnceInjected"));
            province.getVaccinationData().setTotalFullyInjected(object.getInt("totalTwiceInjected"));
            province.getVaccinationData().setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
            province.getVaccinationData().setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
            province.getVaccinationData().setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
            province.getVaccinationData().setFullyInjectedPercent(
              Util.getPercent(
                province.getVaccinationData().getTotalFullyInjected(),
                province.getPopOverEighteen())
            );

            province.getVaccinationData().setInjectedOneDosePercent(
              Util.getPercent(
                province.getVaccinationData().getTotalInjectedOneDose(),
                province.getPopOverEighteen())
            );

            province.getVaccinationData().setTotalVaccinePercent(
              Util.getPercent(
                province.getVaccinationData().getTotalVaccineReality(),
                province.getPopOverEighteen())
            );

            province.getVaccinationData().setTotalInjectedPercent(
              Util.getPercent(
                province.getVaccinationData().getTotalInjected(),
                province.getPopOverEighteen())
            );
            dataVaccinationRepositories.save(province.getVaccinationData());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }


  private void updateCovidStatisticsData(Province province) throws IOException, InterruptedException {
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


  public void updateDataNewCases(Province province) throws IOException {
    Document document = Jsoup.connect(APIData.newCasesByDate.getApi()).timeout(500000).get();
    Elements body = document.select("body").select("table#sailorTable");
    if (body != null) {
      body.select("tbody tr").forEach(element -> {
        if (element.child(0).text().contains(province.getShortName())) {
          province.getCovidData().getDataHistory().forEach(e -> {
            if (UtilDate.today.minusDays(1).isEqual(e.getDate())) {
              province.getCovidData().setYesterday(e.getNewCases());
            }
          });
          province.getCovidData().setToday(Integer.parseInt(element.child(2).text().replaceAll("[^0-9]", "")));
          covidStatisticsRepositories.save(province.getCovidData());
          DataHistory dataHistory = dataHistoryRepositories.findByDate(province.getProvinceCode(), UtilDate.today);
          if (dataHistory != null) {
            dataHistory.setNewCases(Integer.parseInt(element.child(2).text().replaceAll("[^0-9]", "")));
            dataHistoryRepositories.save(dataHistory);
          } else {
            DataHistory newCasesDate = new DataHistory();
            newCasesDate.setDate(UtilDate.today);
            newCasesDate.setNewCases(Integer.parseInt(element.child(2).text().replaceAll("[^0-9]", "")));
            newCasesDate.setCovidData(province.getCovidData());
            dataHistoryRepositories.save(newCasesDate);
          }
          logger.info("Thread-" + Thread.currentThread().getId() + Message.updateDataProvince + province.getName());

        }
      });
    }
  }


  @Async("taskExecutor")
  @Scheduled(cron = "0 3 6,18,10 * * *")
  public void multithreading() throws InterruptedException, IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.supplyAsync(() -> updateVaccinationStatisticsData(provinceCode)).thenApplyAsync(province -> {
        try {
          updateCovidStatisticsData(province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
        return province;
      }).thenApplyAsync(province -> {
        try {
          updateDataNewCases(province);
        } catch (IOException e) {
          e.printStackTrace();
        }
        return province;
      });
    }
  }
}

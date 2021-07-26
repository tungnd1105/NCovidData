package com.ncovid.multithreading;

import com.ncovid.entity.HistoryDataCovidVN;
import com.ncovid.entity.StatisticCovidVN;
import com.ncovid.repositories.HistoryDataCovidVNRepositories;
import com.ncovid.repositories.StatisticCovidVNRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: data covid 19 of all province/city in Vietnam
 */

@Service
public class DataCovidVietnam {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  HistoryDataCovidVNRepositories historyDataCovidVNRepositories;

  @Autowired
  StatisticCovidVNRepositories statisticCovidVNRepositories;

  private void insertDataCovidByDate(int numberOfThread, String province) throws IOException, InterruptedException {
    historyDataCovidVNRepositories.deleteAll();
    logger.info("Threading-" + numberOfThread + " " + "is running insert data covid by date  of" + " " + province);
    JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = (JSONObject) jsonArray.get(i);
      HistoryDataCovidVN historyData = new HistoryDataCovidVN();
      // check the name of the province to assign tasks for thread  and insert data
      if (jsonObject.get("tinh").toString().matches(province)) {
        for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
          JSONObject dataBydate = (JSONObject) jsonObject.get("data");
          historyData.setProvince(jsonObject.get("tinh").toString());
          historyData.setDate(date);
          historyData.setValue(Integer.parseInt(dataBydate.get(date.toString()).toString()));
          historyData.setToday(Integer.parseInt(jsonObject.get("ngay_hien_tai").toString()));
          historyData.setYesterday(Integer.parseInt(jsonObject.get("ngay_truoc_do").toString()));
          historyDataCovidVNRepositories.save(historyData);
        }
      }
    }
    logger.info("Threading-" + numberOfThread + " " + "insert data covid by date of" + " " + province + " " + " " + "completed");
  }


  private void insertDataCovidByProvince(int numberOfThread, String province) throws IOException, InterruptedException {
    logger.info("Threading-" + numberOfThread + " " + "is running insert data statistic covid of"+ " " + province);
    List<HistoryDataCovidVN> historyDataCovidVNList = historyDataCovidVNRepositories.findByProvince(province);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
    if (historyDataCovidVNList.size() != 0) {
      historyDataCovidVNList.forEach(data -> {
        JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
        for (int i = 0; i < jsonArray.length(); i++) {
          StatisticCovidVN statisticCovidVN = new StatisticCovidVN();
          JSONObject object = (JSONObject) jsonArray.get(i);
          if (object.get("tinh").toString().matches(data.getProvince())) {
            statisticCovidVN.setProvince(object.get("tinh").toString());
            statisticCovidVN.setCases(Integer.parseInt(object.get("so_ca").toString()));
            statisticCovidVN.setDeaths(Integer.parseInt(object.get("tu_vong").toString()));
            statisticCovidVN.setDomesticCases(Integer.parseInt(object.get("cong_dong").toString()));
            statisticCovidVN.setEntryCases(Integer.parseInt(object.get("nhap_canh").toString()));
            statisticCovidVN.setDataByDate(data);
            statisticCovidVNRepositories.save(statisticCovidVN);
          }
        }
      });
    }
    logger.info("Threading-" + numberOfThread + " " + "insert data statistic covid of"+ " " + province  + " "+ "completed");
  }

  /**
   * use Multithreading to insert data over 1000 row
   * update data  at 6am everyday
   */
  @EventListener(ApplicationReadyEvent.class)
  public void runMultithreading() throws IOException, InterruptedException {
    List<String> allProvince = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();

    for (String province : allProvince) {
      CompletableFuture.runAsync(() -> {
        numberOfThread.getAndIncrement();
        try {
          insertDataCovidByDate(numberOfThread.get(), province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }).thenRun(() -> {
        try {
          insertDataCovidByProvince(numberOfThread.get(), province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }
}

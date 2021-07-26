package com.ncovid.data.multithreading;

import com.ncovid.entity.DataHistoryVietnam;
import com.ncovid.entity.StatisticalDataVietnam;
import com.ncovid.repositories.DHVietnamRepositories;
import com.ncovid.repositories.SDVietnamRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: data covid 19 of all province/city in Vietnam
 */

@Service
public class DataCovidVietnam {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  DHVietnamRepositories DHVietnamRepositories;

  @Autowired
  SDVietnamRepositories SDVietnamRepositories;

  private void insertDataHistoryVietnam(int threadingNumber, String province) throws IOException, InterruptedException {
    logger.info("Threading-" + threadingNumber + " " + "is running insert data covid by date  of" + " " + province);
    JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = (JSONObject) jsonArray.get(i);
      DataHistoryVietnam DHVietnam = new DataHistoryVietnam();

      if (jsonObject.get("tinh").toString().matches(province)) {
        for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
          JSONObject dataByDate = (JSONObject) jsonObject.get("data");
          DHVietnam.setDate(date);
          DHVietnam.setProvince(jsonObject.get("tinh").toString());
          DHVietnam.setValue(Integer.parseInt(dataByDate.get(date.toString()).toString()));
          DHVietnam.setToday(Integer.parseInt(jsonObject.get("ngay_hien_tai").toString()));
          DHVietnam.setYesterday(Integer.parseInt(jsonObject.get("ngay_truoc_do").toString()));
          DHVietnamRepositories.save(DHVietnam);

        }
      }
    }
    logger.info("Threading-" + threadingNumber + " " + "insert data covid by date of" + " " + province + " " + " " + "completed");
  }

  private void insertStatisticalDataVietnam(int threadingNumber, String province) throws IOException, InterruptedException {
    logger.info("Threading-" + threadingNumber + " " + "is running insert data statistic covid of" + " " + province);
    List<DataHistoryVietnam> DHVietnamList = DHVietnamRepositories.findByProvince(province);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
    if (DHVietnamList.size() != 0) {
      DHVietnamList.forEach(data -> {

        JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject object = (JSONObject) jsonArray.get(i);
          StatisticalDataVietnam SDVietnam = new StatisticalDataVietnam();

          if (object.get("tinh").toString().matches(data.getProvince())) {
            SDVietnam.setProvince(object.get("tinh").toString());
            SDVietnam.setCases(Integer.parseInt(object.get("so_ca").toString()));
            SDVietnam.setDeaths(Integer.parseInt(object.get("tu_vong").toString()));
            SDVietnam.setDomesticCases(Integer.parseInt(object.get("cong_dong").toString()));
            SDVietnam.setEntryCases(Integer.parseInt(object.get("nhap_canh").toString()));
            SDVietnam.setDataByDate(data);
            SDVietnamRepositories.save(SDVietnam);

          }
        }
      });
    }
    logger.info("Threading-" + threadingNumber + " " + "insert data statistic covid of" + " " + province + " " + "completed");
  }


  /**
   * use Multithreading to insert data over 1000 row
   * each threading will be insert data covid of province
   * update data  at 6am everyday
   * run async:  method insertDataCovidByDate completed task ->  run method insertDataCovidByProvince
   */
  @Scheduled(cron = "* * 6 * * *")
  public void runMultithreading() throws IOException, InterruptedException {
    List<StatisticalDataVietnam> dataExist = SDVietnamRepositories.findAll();
    // check data not yet in database
    if (dataExist.size() != 0) {
      logger.info("starting drop data and update new data");
      // refresh data for update new data
      SDVietnamRepositories.deleteAll();
      DHVietnamRepositories.deleteAll();
    }

    List<String> allProvince = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();

    for (String province : allProvince) {
      CompletableFuture.runAsync(() -> {
        numberOfThread.getAndIncrement();
        try {
          insertDataHistoryVietnam(numberOfThread.get(), province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }).thenRun(() -> {
        try {
          insertStatisticalDataVietnam(numberOfThread.get(), province);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      });
    }

  }
}

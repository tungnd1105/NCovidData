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
  private DHVietnamRepositories DHVietnamRepositories;

  @Autowired
  private SDVietnamRepositories SDVietnamRepositories;

  private void insertDataHistoryVietnam(int threadingNumber, Integer provinceCode) throws IOException, InterruptedException {
    logger.info("Threading-" + threadingNumber + " " + "is running insert data covid by date  of" + " " + provinceCode);
    JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = (JSONObject) jsonArray.get(i);
      if (jsonObject.get("ma").equals(provinceCode)) {
        for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
          DataHistoryVietnam DHVietnam = new DataHistoryVietnam();
          JSONObject dataByDate = (JSONObject) jsonObject.get("data");
          DHVietnam.setDate(date);
          DHVietnam.setProvinceCode(Integer.parseInt(jsonObject.get("ma").toString()));
          DHVietnam.setValue(Integer.parseInt(dataByDate.get(date.toString()).toString()));
          DHVietnamRepositories.save(DHVietnam);
        }
      }
    }
    logger.info("Threading-" + threadingNumber + " " + "insert data covid by date of" + " " + provinceCode + " " + " " +
      "completed");
  }

  private void insertStatisticalDataVietnam(int threadingNumber, Integer provinceCode) throws IOException, InterruptedException {
    logger.info("Threading-" + threadingNumber + " " + "is running insert data statistic covid of" + " " + provinceCode);
    List<DataHistoryVietnam> DHVietnamList = DHVietnamRepositories.findByProvinceCode(provinceCode);
    JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
    JSONArray jsonArray1 = (JSONArray) jsonObject.get("rows");
    JSONArray jsonArray2 = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));

    for (int j =0; j < jsonArray1.length(); j++){
      JSONObject object1 = (JSONObject) jsonArray1.get(j);
      StatisticalDataVietnam SDVietnam = new StatisticalDataVietnam();
      for (int i = 0; i < jsonArray2.length(); i++){
        JSONObject object2 = (JSONObject) jsonArray2.get(i);
        if(object1.get("tinh").toString().matches(object2.get("tinh").toString())){
          if(object2.get("ma").equals(provinceCode)){
            SDVietnam.setProvinceCode(Integer.parseInt(object2.get("ma").toString()));
            SDVietnam.setToday(Integer.parseInt(object2.get("ngay_hien_tai").toString()));
            SDVietnam.setYesterday(Integer.parseInt(object2.get("ngay_truoc_do").toString()));
            SDVietnam.setCases(Integer.parseInt(object1.get("so_ca").toString()));
            SDVietnam.setDeaths(Integer.parseInt(object1.get("tu_vong").toString()));
            SDVietnam.setDomesticCases(Integer.parseInt(object1.get("cong_dong").toString()));
            SDVietnam.setEntryCases(Integer.parseInt(object1.get("nhap_canh").toString()));
            SDVietnam.setUpdateAt(Util.today);
            DHVietnamList.removeIf(e -> !e.getProvinceCode().equals(SDVietnam.getProvinceCode()));
            SDVietnam.setDataByDate(DHVietnamList);
            SDVietnamRepositories.save(SDVietnam);
            logger.info("Threading-" + threadingNumber + " " + "insert data statistic covid of" + " " + object2.get("tinh").toString() + " " + "completed");
          }
        }
      }

    }

  }

  /**
   * use Multithreading to insert data over 1000 row
   * each threading will be insert data covid of province
   * update data  at 6am everyday
   * run async:  method insertDataCovidByDate completed task ->  run method insertDataCovidByProvince
   */
  @Scheduled(cron = "10 * * * * *")
  private void runMultithreading() throws IOException, InterruptedException {
    List<StatisticalDataVietnam> dataExist = SDVietnamRepositories.findAll();
    // check data not yet in database
    if (dataExist.size() != 0) {
      logger.info("starting drop data and update new data");
      // refresh data for update new data
      SDVietnamRepositories.deleteAll();
      DHVietnamRepositories.deleteAll();
    }

    List<Integer> provincodeList = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();

    for (Integer provinceCode : provincodeList) {
      CompletableFuture.runAsync(() -> {
        numberOfThread.getAndIncrement();
        try {
          insertDataHistoryVietnam(numberOfThread.get(), provinceCode);
          logger.info("insert data covid by date of all province completed");
        } catch (IOException | InterruptedException e) {
          logger.error(e.getMessage());
          logger.error("Threading-" + numberOfThread + "" + "interruptedException");
        }
      }).thenRun(() -> {
        try {
          insertStatisticalDataVietnam(numberOfThread.get(), provinceCode);
          logger.info("insert data statistical covid of province completed");
        } catch (IOException | InterruptedException e) {
          logger.error(e.getMessage());
          logger.error("Threading-" + numberOfThread + "" + "interruptedException");
        }
      });
    }

  }
}


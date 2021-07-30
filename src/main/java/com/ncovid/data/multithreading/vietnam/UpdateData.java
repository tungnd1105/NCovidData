package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.StatisticalCovidRepositories;
import com.ncovid.repositories.vietnam.StatisticalVaccineRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 * description class: update data covid, vaccine of all province
 */
@Service
public class UpdateData {


  public static Logger logger = LoggerFactory.getLogger(UpdateData.class);

  @Autowired
  private StatisticalCovidRepositories SCovidRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;

  @Autowired
  private StatisticalVaccineRepositories SVaccineRepositories;

  private void updateDataCovidToday(Integer provinceCode) {
    try {
      DataHistory checkToday = dataHistoryRepositories.findByDateAndProvinceCode(provinceCode, Util.today);
      if (checkToday == null) {
        JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = (JSONObject) jsonArray.get(i);
          if (jsonObject.getInt("ma") == provinceCode) {
            DataHistory dataToday = new DataHistory();
            JSONObject object = (JSONObject) jsonObject.get("data");
            dataToday.setProvinceCode(jsonObject.getInt("ma"));
            dataToday.setDate(Util.today);
            dataToday.setValue(object.getInt(Util.today.toString()));
            dataHistoryRepositories.save(dataToday);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateStatisticalCovid(Integer number, Integer provinceCode) {
    try {
      DataHistory checkToday = dataHistoryRepositories.findByDateAndProvinceCode(provinceCode, Util.today);
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
        JSONArray jsonArray1 = (JSONArray) jsonObject.get("rows");
        JSONArray jsonArray2 = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
        for (int k = 0; k < jsonArray1.length(); k++) {
          JSONObject object1 = (JSONObject) jsonArray1.get(k);
          for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject object2 = (JSONObject) jsonArray2.get(i);
            if (object1.getString("tinh").contains(province.getShortName())
              && object2.getString("tinh").contains(province.getShortName())) {
              province.getDataCovid().setUpdateTime(Util.timeUpdate);
              province.getDataCovid().setCases(object1.getInt("so_ca"));
              province.getDataCovid().setDeaths(object1.getInt("tu_vong"));
              province.getDataCovid().setToday(object2.getInt("ngay_hien_tai"));
              province.getDataCovid().setYesterday(object2.getInt("ngay_truoc_do"));
              province.getDataCovid().setDomesticCases(object1.getInt("cong_dong"));
              province.getDataCovid().setEntryCases(object1.getInt("nhap_canh"));
              if (checkToday == null) {
                DataHistory dataToday = dataHistoryRepositories.findByDateAndProvinceCode(provinceCode, Util.today);
                province.getDataCovid().getDataByDate().add(dataToday);
              }
              SCovidRepositories.save(province.getDataCovid());
            }
          }
        }
        logger.info("Threading-" + number + " data covid of " + province.getName() + " has been updated");
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Threading-" + number + " handle exception");
    }
  }

  private void updateStatisticalVaccine(Integer number, Integer provinceCode) {
    try {
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataVaccine));
        for (int k = 0; k < jsonArray.length(); k++) {
          JSONObject object = (JSONObject) jsonArray.get(k);
          if (object.getInt("provinceCode") == province.getProvinceCode()) {
            province.getDataVaccine().setUpdateTime(Util.timeUpdate);
            province.getDataVaccine().setProvinceCode(object.getInt("provinceCode"));
            province.getDataVaccine().setTotalVaccinated(object.getInt("totalInjected"));
            province.getDataVaccine().setTotalOnceInjected(object.getInt("totalOnceInjected"));
            province.getDataVaccine().setTotalTwiceInjected(object.getInt("totalTwiceInjected"));
            province.getDataVaccine().setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
            province.getDataVaccine().setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
            province.getDataVaccine().setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
            SVaccineRepositories.save(province.getDataVaccine());
          }
        }
        logger.info("Threading-" + number + " data vaccine of " + province.getName() + " has been updated");
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Threading-" + number + " handle exception");
    }
  }


  /**
   * update data realtime
   * use multithreading to  insert data faster
   * each threading will be update data covid, vaccine of provinces by province code
   * 0PM,6Am,12AM,8PM everyday
   */
  @Async("taskExecutor")
  @Scheduled(cron = " * * 0,6,12,8 * * *")
  public void multithreading() throws InterruptedException, IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.runAsync(() -> updateDataCovidToday(provinceCode))
        .thenRun(() -> updateStatisticalCovid(numberOfThread.get(), provinceCode));
      CompletableFuture.runAsync(() -> updateStatisticalVaccine(numberOfThread.get(), provinceCode));
    }
  }

}

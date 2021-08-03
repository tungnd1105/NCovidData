package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.StatisticalCovidRepositories;
import com.ncovid.repositories.vietnam.StatisticalVaccineRepositories;
import com.ncovid.util.Message;
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


  private void updateStatisticalVaccineData(Integer provinceCode) {
    try {
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataVaccine));
        for (int k = 0; k < jsonArray.length(); k++) {
          JSONObject object = (JSONObject) jsonArray.get(k);
          if (object.getInt("provinceCode") == province.getProvinceCode()) {
            province.getVaccineData().setUpdateTime(Util.timeUpdate);
            province.getVaccineData().setTotalVaccinated(object.getInt("totalInjected"));
            province.getVaccineData().setTotalOnceInjected(object.getInt("totalOnceInjected"));
            province.getVaccineData().setTotalTwiceInjected(object.getInt("totalTwiceInjected"));
            province.getVaccineData().setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
            province.getVaccineData().setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
            province.getVaccineData().setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
            SVaccineRepositories.save(province.getVaccineData());
          }
        }
        logger.info("Thread-" + Thread.currentThread().getId() + Message.updateDataVaccine + province.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  private void updateStatisticalCovidData(Integer provinceCode) {
    try {
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
      JSONArray jsonArray1 = (JSONArray) jsonObject.get("rows");
      JSONArray jsonArray2 = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      if (province != null) {
        for (int k = 0; k < jsonArray1.length(); k++) {
          JSONObject object1 = (JSONObject) jsonArray1.get(k);
          for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject object2 = (JSONObject) jsonArray2.get(i);
            if (object2.getInt("ma") == province.getProvinceCode()
              && object1.getString("tinh").matches(object2.getString("tinh"))) {
              province.getCovidData().setUpdateTime(Util.timeUpdate);
              province.getCovidData().setCases(object1.getInt("so_ca"));
              province.getCovidData().setDeaths(object1.getInt("tu_vong"));
              province.getCovidData().setToday(object2.getInt("ngay_hien_tai"));
              province.getCovidData().setYesterday(object2.getInt("ngay_truoc_do"));
              province.getCovidData().setDomesticCases(object1.getInt("cong_dong"));
              province.getCovidData().setEntryCases(object1.getInt("nhap_canh"));
              SCovidRepositories.save(province.getCovidData());
            }

          }
        }
        logger.info("Thread-" + Thread.currentThread().getId() + Message.updateDataCovid + province.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  private void updateDataNewCases(Integer provinceCode) {
    try {
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      if (province != null) {
        province.getCovidData().getDataHistory().forEach(e -> {
          if (!e.getDate().isEqual(Util.today)) {
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jsonObject = (JSONObject) jsonArray.get(i);
              if (jsonObject.getInt("ma") == province.getProvinceCode()) {
                DataHistory dataToday = new DataHistory();
                JSONObject object = (JSONObject) jsonObject.get("data");
                dataToday.setDate(Util.today);
                dataToday.setNewCases(object.getInt(Util.today.toString()));
                dataToday.setCovidData(province.getCovidData());
                dataHistoryRepositories.save(dataToday);
              }
            }
          }
        });
        logger.info("Thread-" + Thread.currentThread().getId() + Message.updateDataCovidByDate + Util.today + " of province" + province.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  /**
   * update data realtime
   * use multithreading to  insert data faster
   * each threading will be flow task
   * updateStatisticalVaccineData -> updateStatisticalCovidData -> updateCovidDataByDate
   * 0PM o'clock,6Am o'clock ,12AM o'clock,8PM o'clock everyday
   */
  @Async("taskExecutor")
  @Scheduled(cron = "0 0 6,12,20,0 * * * ")
  public void multithreading() throws InterruptedException, IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.runAsync(() -> updateStatisticalVaccineData(provinceCode))
        .thenRun(() -> updateStatisticalCovidData(provinceCode))
        .thenRun(() -> updateDataNewCases(provinceCode));
    }
  }
}
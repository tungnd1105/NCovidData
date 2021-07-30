package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.StatisticalCovid;
import com.ncovid.entity.vietnam.StatisticalVaccine;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: insert new data covid 19 of all province/city in Vietnam
 */

@Service
public class DataCovidVietnam {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  private DataHistoryRepositories DataHistoryRepositories;

  @Autowired
  private StatisticalCovidRepositories SCovidRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private StatisticalVaccineRepositories SVaccineRepositories;


  private void insertDataCovidByDate(int number, Integer provinceCode) {
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
        if (jsonObject.get("ma").equals(provinceCode)) {
          for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
            DataHistory dataHistory = new DataHistory();
            JSONObject dataByDate = (JSONObject) jsonObject.get("data");
            dataHistory.setDate(date);
            dataHistory.setProvinceCode(jsonObject.getInt("ma"));
            dataHistory.setValue(dataByDate.getInt(date.toString()));
            DataHistoryRepositories.save(dataHistory);
          }
        }
      }
      logger.info("Threading-" + number + " data covid by date province code " + provinceCode + " has been saved");
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  private void insertStatisticalDataCovid(int threadingNumber, Integer provinceCode) {
    try {
      List<DataHistory> dataHistoryList = DataHistoryRepositories.findByProvinceCode(provinceCode);
      JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
      JSONArray jsonArray1 = (JSONArray) jsonObject.get("rows");
      JSONArray jsonArray2 = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      for (int j = 0; j < jsonArray1.length(); j++) {
        JSONObject object1 = (JSONObject) jsonArray1.get(j);
        for (int i = 0; i < jsonArray2.length(); i++) {
          JSONObject object2 = (JSONObject) jsonArray2.get(i);
          if (object1.get("tinh").toString().matches(object2.get("tinh").toString())) {
            if (object2.get("ma").equals(provinceCode)) {
              StatisticalCovid SCovid = new StatisticalCovid();
              SCovid.setProvinceCode(object2.getInt("ma"));
              SCovid.setToday(object2.getInt("ngay_hien_tai"));
              SCovid.setYesterday(object2.getInt("ngay_truoc_do"));
              SCovid.setCases(object1.getInt("so_ca"));
              SCovid.setDeaths(object1.getInt("tu_vong"));
              SCovid.setDomesticCases(object1.getInt("cong_dong"));
              SCovid.setEntryCases(object1.getInt("nhap_canh"));
              SCovid.setUpdateTime(Util.timeUpdate);
              dataHistoryList.removeIf(e -> !e.getProvinceCode().equals(SCovid.getProvinceCode()));
              dataHistoryList.sort(Comparator.comparing(DataHistory::getDate));
              SCovid.setDataByDate(dataHistoryList);
              SCovidRepositories.save(SCovid);
            }
          }
        }
      }
      logger.info("Threading-" + threadingNumber + " data statistical covid province code " + provinceCode + " has been saved");

    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      logger.warn("Threading-" + threadingNumber + " handle exception");
    }
  }

  private void insertStatisticalDataVaccine(int threadingNumber, Integer provinceCode) {
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataVaccine));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject object = (JSONObject) jsonArray.get(k);
        if (object.getInt("provinceCode") == provinceCode) {
          StatisticalVaccine SVaccine = new StatisticalVaccine();
          SVaccine.setUpdateTime(Util.timeUpdate);
          SVaccine.setProvinceCode(object.getInt("provinceCode"));
          SVaccine.setTotalVaccinated(object.getInt("totalInjected"));
          SVaccine.setTotalOnceInjected(object.getInt("totalOnceInjected"));
          SVaccine.setTotalTwiceInjected(object.getInt("totalTwiceInjected"));
          SVaccine.setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
          SVaccine.setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
          SVaccine.setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
          SVaccineRepositories.save(SVaccine);
        }
      }
      logger.info("Threading-" + threadingNumber + " data vaccine province code " + provinceCode + " has been saved");
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Threading-" + threadingNumber + " handle exception");
    }
  }

  private void insertDataCovidOfProvince(int threadingNumber, Integer provinceCode) {
    try {
      JSONArray jsonDataProvinceArray = new JSONArray(Util.fetchDataJson(Util.urlDataAllProince));
      JSONArray jsonDataPopulationArray = new JSONArray(Util.fetchDataJson(Util.urlDataPopulationOfProince));
      for (int k = 0; k < jsonDataProvinceArray.length(); k++) {
        JSONObject object = (JSONObject) jsonDataProvinceArray.get(k);
        if (object.getInt("provinceCode") == provinceCode) {
          Province province = new Province();
          province.setProvinceCode(object.getInt("provinceCode"));
          province.setShortName(object.getString("shortName"));
          province.setName(object.getString("name"));
          province.setType(object.getString("type"));
          for (int a = 0; a < jsonDataPopulationArray.length(); a++) {
            JSONObject object2 = (JSONObject) jsonDataPopulationArray.get(a);
            if (object2.getString("provinceName").matches(province.getShortName())) {
              province.setPopOverEighteen(object2.getInt("popOverEighteen"));
              province.setTotalPopulation(object2.getInt("population"));
              SCovidRepositories.findById(province.getProvinceCode()).ifPresent(province::setDataCovid);
              SVaccineRepositories.findById(province.getProvinceCode()).ifPresent(province::setDataVaccine);
              provinceRepositories.save(province);
            }
          }
        }
      }
      logger.info("Threading-" + threadingNumber + " data covid and vaccine by province has been saved ");

    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      logger.warn("Threading-" + threadingNumber + " handle exception");
    }
  }

  /**
   * if data not yet in databases run insert new
   * use multithreading to  insert data faster
   * each threading will be insert data covid, vaccine of provinces  by province code
   * each threading flow task
   * insertDataCovidByDate -> insertStatisticalDataCovid -> insertStatisticalDataVaccine -> insertDataAllProvince
   */
  @EventListener(ApplicationReadyEvent.class)
  public void runMultithreading() throws IOException, InterruptedException {
    List<Province> dataExist = provinceRepositories.findAll();
    if (dataExist.size() == 0) {
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      AtomicInteger numberOfThread = new AtomicInteger();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture.runAsync(() -> {
          numberOfThread.getAndIncrement();
          insertDataCovidByDate(numberOfThread.get(), provinceCode);
        })
          .thenRun(() -> insertStatisticalDataCovid(numberOfThread.get(), provinceCode))
          .thenRun(() -> insertStatisticalDataVaccine(numberOfThread.get(), provinceCode))
          .thenRun(() -> insertDataCovidOfProvince(numberOfThread.get(), provinceCode));
      }
    }
  }

}


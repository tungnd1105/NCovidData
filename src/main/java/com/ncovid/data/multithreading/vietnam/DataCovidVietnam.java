package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.vietnam.CovidStatistics;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.VaccinationStatistics;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.VaccinationStatisticsRepositories;
import com.ncovid.util.Message;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
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
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: insert new data covid 19 and Vaccination  of all province/city in Vietnam
 */

@Service
public class DataCovidVietnam {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  private DataHistoryRepositories DataHistoryRepositories;

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;


  private void insertDataInfoOfProvince(Integer provinceCode) {
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
          province.setCountry("Vietnam");
          for (int a = 0; a < jsonDataPopulationArray.length(); a++) {
            JSONObject object2 = (JSONObject) jsonDataPopulationArray.get(a);
            if (object2.getString("provinceName").matches(province.getShortName())) {
              province.setPopOverEighteen(object2.getInt("popOverEighteen"));
              province.setTotalPopulation(object2.getInt("population"));
              provinceRepositories.save(province);
            }
          }
        }
      }
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  private void insertVaccinationStatisticsData(Integer provinceCode) {
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataVaccinations));
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        for (int k = 0; k < jsonArray.length(); k++) {
          JSONObject object = (JSONObject) jsonArray.get(k);
          if (object.getInt("provinceCode") == province.getProvinceCode()) {
            VaccinationStatistics dataVaccination = new VaccinationStatistics();
            dataVaccination.setUpdateTime(Util.timeUpdate);
            dataVaccination.setTotalInjected(object.getInt("totalInjected"));
            dataVaccination.setTotalInjectedOneDose(object.getInt("totalOnceInjected"));
            dataVaccination.setTotalFullyInjected(object.getInt("totalTwiceInjected"));
            dataVaccination.setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
            dataVaccination.setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
            dataVaccination.setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
            dataVaccination.setProvince(province);
            dataVaccinationRepositories.save(dataVaccination);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  private void insertCovidStatisticsData(Integer provinceCode) {
    try {
      JSONObject jsonObject = new JSONObject(Util.fetchDataJson(Util.urlDataProvinceType));
      JSONArray jsonArray1 = (JSONArray) jsonObject.get("rows");
      JSONArray jsonArray2 = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        for (int j = 0; j < jsonArray1.length(); j++) {
          JSONObject object1 = (JSONObject) jsonArray1.get(j);
          for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject object2 = (JSONObject) jsonArray2.get(i);
            if (object2.getInt("ma") == province.getProvinceCode()
              && object1.getString("tinh").matches(object2.getString("tinh"))) {
              CovidStatistics dataCovid = new CovidStatistics();
              dataCovid.setToday(object2.getInt("ngay_hien_tai"));
              dataCovid.setYesterday(object2.getInt("ngay_truoc_do"));
              dataCovid.setCases(object1.getInt("so_ca"));
              dataCovid.setDeaths(object1.getInt("tu_vong"));
              dataCovid.setDomesticCases(object1.getInt("cong_dong"));
              dataCovid.setEntryCases(object1.getInt("nhap_canh"));
              dataCovid.setUpdateTime(Util.timeUpdate);
              dataCovid.setProvince(province);
              dataCovidRepositories.save(dataCovid);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }

  }

  private void insertDataNewCasesByDate(Integer provinceCode) {
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
      Province province = provinceRepositories.findById(provinceCode).orElse(null);
      if (province != null) {
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = (JSONObject) jsonArray.get(i);
          if (jsonObject.getInt("ma") == province.getProvinceCode()) {
            for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
              DataHistory dataHistory = new DataHistory();
              JSONObject dataByDate = (JSONObject) jsonObject.get("data");
              dataHistory.setDate(date);
              dataHistory.setNewCases(dataByDate.getInt(date.toString()));
              dataHistory.setCovidData(province.getCovidData());
              DataHistoryRepositories.save(dataHistory);
            }
          }
        }
        logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataProvince + province.getName());
      }

    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
  }

  /**
   * if data not yet in databases run insert new
   * use multithreading to  insert data faster
   * each threading will be insert data covid, vaccine of provinces  by province code
   * each threading flow task
   * insertDataAllProvince -> insertVaccinationStatisticsData ->  insertCovidStatisticsData ->  insertDataNewCasesByDate
   */
  @EventListener(ApplicationReadyEvent.class)
  @Async("taskExecutor")
  public void runMultithreading() throws IOException, InterruptedException {
    List<Province> dataExist = provinceRepositories.findAll();
    if (dataExist.size() == 0) {
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture.runAsync(() ->
          insertDataInfoOfProvince(provinceCode))
          .thenRun(() -> insertVaccinationStatisticsData(provinceCode))
          .thenRun(() -> insertCovidStatisticsData(provinceCode))
          .thenRun(() -> insertDataNewCasesByDate(provinceCode));
      }
    }
  }

}


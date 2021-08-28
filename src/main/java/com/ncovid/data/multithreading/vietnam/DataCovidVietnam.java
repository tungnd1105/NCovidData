package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.APIData;
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
import com.ncovid.util.UtilDate;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * note: because cross-origin request source data, use data json  demo
 * description class: insert new data covid 19 and Vaccination  of all province/city in Vietnam
 */

@Service
public class DataCovidVietnam {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;


  private Province insertDataInfoOfProvince(Integer provinceCode) {
    Province province = new Province();
    try {
      JSONArray jsonDataProvinceArray = new JSONArray(Util.fetchDataJson(APIData.detailProvince));
      JSONArray jsonDataPopulationArray = new JSONArray(Util.fetchDataJson(APIData.population));
      for (int k = 0; k < jsonDataProvinceArray.length(); k++) {
        JSONObject object = (JSONObject) jsonDataProvinceArray.get(k);
        if (object.getInt("provinceCode") == provinceCode) {
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
            }
            provinceRepositories.save(province);
          }
        }
      }
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }

  private Province insertVaccinationStatisticsData(Province province) {
    VaccinationStatistics dataVaccination = new VaccinationStatistics();
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(APIData.vaccinationsByProvince));
      if (province != null) {
        for (int k = 0; k < jsonArray.length(); k++) {
          JSONObject object = (JSONObject) jsonArray.get(k);
          if (object.getInt("provinceCode") == province.getProvinceCode()) {
            dataVaccination.setUpdateTime(UtilDate.timeUpdate);
            dataVaccination.setTotalInjected(object.getInt("totalInjected"));
            dataVaccination.setTotalInjectedOneDose(object.getInt("totalOnceInjected"));
            dataVaccination.setTotalFullyInjected(object.getInt("totalTwiceInjected"));
            dataVaccination.setTotalVaccineAllocated(object.getInt("totalVaccineAllocated"));
            dataVaccination.setTotalVaccineReality(object.getInt("totalVaccineAllocatedReality"));
            dataVaccination.setTotalVaccinationLocation(object.getInt("totalVaccinationLocation"));
            dataVaccination.setFullyInjectedPercent(Util.getPercent(dataVaccination.getTotalFullyInjected(), province.getPopOverEighteen()));
            dataVaccination.setInjectedOneDosePercent(Util.getPercent(dataVaccination.getTotalInjectedOneDose(), province.getPopOverEighteen()));
            dataVaccination.setTotalVaccinePercent(Util.getPercent(dataVaccination.getTotalVaccineReality(), province.getPopOverEighteen()));
            dataVaccination.setTotalInjectedPercent(Util.getPercent(dataVaccination.getTotalInjected(), province.getPopOverEighteen()));
          }
          dataVaccination.setProvince(province);
          dataVaccinationRepositories.save(dataVaccination);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return dataVaccination.getProvince();
  }

  private Province insertCovidStatisticsData(Province province) throws IOException, InterruptedException {
    Path pathFile = Paths.get(Util.bodyGraphQl.getAbsolutePath());
    JSONObject jsonObject = new JSONObject(Util.postMapping(APIData.covidByProvince, pathFile));
    JSONObject jsonObject2 = jsonObject.getJSONObject("data");
    JSONArray jsonArray = jsonObject2.getJSONArray("provinces");
    CovidStatistics covidStatistics = new CovidStatistics();

    for (int k = 0; k < jsonArray.length(); k++) {
      JSONObject data = jsonArray.getJSONObject(k);
      String provinceId = data.getString("Province_Id").replaceAll("[^0-9]", "");
      if (province != null) {
        if (province.getProvinceCode() == Integer.parseInt(provinceId)) {
          covidStatistics.setCases(data.getInt("Confirmed"));
          covidStatistics.setDeaths(data.getInt("Deaths"));
          covidStatistics.setRecovered(data.getInt("Recovered"));
          covidStatistics.setCasesPercent(Util.getPercent(covidStatistics.getCases(), province.getTotalPopulation()));
          covidStatistics.setDeathsPercent(Util.getPercent(covidStatistics.getDeaths(), province.getPopOverEighteen()));
          covidStatistics.setRecoveredPercent(Util.getPercent(covidStatistics.getRecovered(), province.getTotalPopulation()));
        }
        covidStatistics.setUpdateTime(UtilDate.timeUpdate);
        covidStatistics.setProvince(province);
        dataCovidRepositories.save(covidStatistics);
      }
    }
    return provinceRepositories.findById(province.getProvinceCode()).orElse(null);
  }

  private Province insertDataNewCasesByDate(Province province) {
    try {
      JSONArray jsonArray = new JSONArray(new String(Files.readAllBytes(Paths.get(Util.covidBydate.getAbsolutePath()))));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject jsonObject = jsonArray.getJSONObject(k);
        if (jsonObject.getInt("ma") == province.getProvinceCode()) {
          JSONObject dataByDate = (JSONObject) jsonObject.get("data");
          for (LocalDate date = UtilDate.startDate; date.isBefore(UtilDate.today); date = date.plusDays(1)) {
            DataHistory dataHistory = new DataHistory();
            dataHistory.setDate(date);
            dataHistory.setNewCases(dataByDate.getInt(date.toString()));
            dataHistory.setCovidData(province.getCovidData());
            dataHistoryRepositories.save(dataHistory);
          }
        }
      }
      logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataProvince + province.getName());
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }

  /**
   * if data not yet in databases run insert new
   * use multithreading to performance optimization
   * each threading will be insert data covid, vaccine of provinces  by province code
   * each threading flow task
   * insertDataAllProvince -> insertVaccinationStatisticsData ->  insertCovidStatisticsData
   */
  @EventListener(ApplicationReadyEvent.class)
  @Async("taskExecutor")
  public void runMultithreading() throws IOException, InterruptedException {
    List<Province> dataExist = provinceRepositories.findAll();
    if (dataExist.size() == 0) {
      logger.info("staring assign task for threading by province code ");
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture.supplyAsync(() -> insertDataInfoOfProvince(provinceCode))
          .thenApplyAsync(this::insertVaccinationStatisticsData).thenApplyAsync(province -> {
          try {
            province = insertCovidStatisticsData(province);
          } catch (IOException | InterruptedException e) {
            e.printStackTrace();
          }
          return province;
        }).thenApplyAsync(this::insertDataNewCasesByDate);
      }
    }
  }

}


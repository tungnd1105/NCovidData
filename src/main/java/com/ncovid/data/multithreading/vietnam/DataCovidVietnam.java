package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.CovidStatistics;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.VaccinationStatistics;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
              provinceRepositories.save(province);
            }
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
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(APIData.vaccinationsByProvince));
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
            dataVaccination.setFullyInjectedPercent(Util.getPercent(dataVaccination.getTotalFullyInjected(), province.getPopOverEighteen()));
            dataVaccination.setInjectedOneDosePercent(Util.getPercent(dataVaccination.getTotalInjectedOneDose(), province.getPopOverEighteen()));
            dataVaccination.setTotalVaccinePercent(Util.getPercent(dataVaccination.getTotalVaccineReality(), province.getPopOverEighteen()));
            dataVaccination.setTotalInjectedPercent(Util.getPercent(dataVaccination.getTotalInjected(), province.getPopOverEighteen()));
            dataVaccination.setProvince(province);
            dataVaccinationRepositories.save(dataVaccination);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }

  private void insertCovidStatisticsData(Province province) throws IOException, InterruptedException {
    Path pathFile = Paths.get(Util.bodyGraphQl.getAbsolutePath());
    JSONObject jsonObject = new JSONObject(Util.postMapping(APIData.covidByProvince, pathFile));
    JSONObject jsonObject2 = jsonObject.getJSONObject("data");
    JSONArray jsonArray = jsonObject2.getJSONArray("provinces");
    for (int k = 0; k < jsonArray.length(); k++) {
      JSONObject data = jsonArray.getJSONObject(k);
      String provinceId = data.getString("Province_Id").replaceAll("[^0-9]", "");
      if (province.getProvinceCode() == Integer.parseInt(provinceId)) {
        CovidStatistics covidStatistics = new CovidStatistics();
        covidStatistics.setUpdateTime(Util.timeUpdate);
        covidStatistics.setCases(data.getInt("Confirmed"));
        covidStatistics.setDeaths(data.getInt("Deaths"));
        covidStatistics.setRecovered(data.getInt("Recovered"));
        covidStatistics.setCasesPercent(Util.getPercent(covidStatistics.getCases(), province.getTotalPopulation()));
        covidStatistics.setDeathsPercent(Util.getPercent(covidStatistics.getDeaths(), province.getPopOverEighteen()));
        covidStatistics.setRecoveredPercent(Util.getPercent(covidStatistics.getRecovered(), province.getTotalPopulation()));
        covidStatistics.setProvince(province);
        dataCovidRepositories.save(covidStatistics);
      }
    }
    logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataProvince + province.getName());
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
    logger.info("staring assign task for threading by province code ");
    if (dataExist.size() == 0) {
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture<Province> completableFuture =
          CompletableFuture.supplyAsync(() -> insertDataInfoOfProvince(provinceCode))
            .thenApply(this::insertVaccinationStatisticsData);

        completableFuture.thenRun(() -> {
          try {
            insertCovidStatisticsData(completableFuture.get());
          } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
          }
        });
      }
    }
  }

}


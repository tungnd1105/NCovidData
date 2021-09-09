package com.ncovid.data.multithreading.vietnam;


import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.Province;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project CovidStatistics.java
 * @Date 27/08/2021
 */

@Service
public class UpdateDataVaccination {

  public static Logger logger = LoggerFactory.getLogger(UpdateDataVaccination.class);

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

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
        logger.info("Threading-" + Thread.currentThread().getId() + Message.updateDataVaccinationProvince + province.getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Thread-" + Thread.currentThread().getId() + " handle exception");
    }
    return province;
  }


  // update binally two day
  @Async("taskExecutor")
  @Scheduled(cron = "* * * 2 * *")
  public void multithreading() throws IOException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture.supplyAsync(() -> updateVaccinationStatisticsData(provinceCode));
    }
  }

}
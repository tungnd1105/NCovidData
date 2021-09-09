package com.ncovid.data.multithreading.vietnam;

import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.VaccinationStatistics;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project VaccinationStatistics.java
 * @Date 09/09/2021
 */

@Service
public class DataVaccination {

  public static Logger logger = LoggerFactory.getLogger(DataVaccination.class);

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private VaccinationStatisticsRepositories dataVaccinationRepositories;

  private Province insertVaccinationStatisticsData(Integer provinceCode) {
    Province province = provinceRepositories.findById(provinceCode).orElse(null);
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
    logger.info("Threading-"+ Thread.currentThread().getId() + Message.insertDataVaccinationProvince + province.getName());
    return dataVaccination.getProvince();
  }

//  @EventListener(ApplicationReadyEvent.class)
//  @Async("taskExecutor")
  public void runMultithreading() throws IOException {
    List<VaccinationStatistics> dataExist = dataVaccinationRepositories.findAll();
    if (dataExist.size() == 0) {
      logger.info("staring assign task for threading by province code ");
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture.supplyAsync(() -> insertVaccinationStatisticsData(provinceCode));
      }
    }
  }

}

package com.ncovid.services.multithreading.vietnam;

import com.ncovid.data.multithreading.vietnam.DataCovidVietnam;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.services.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: receive and process requests get data covid  from the controller
 */

@Service
public class VietnamServices {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  ProvinceRepositories provinceRepositories;

  public ResponseEntity<Province> findDataByOneProvince(Integer provinceCode, String name) {
    Province dataOfProvince = provinceRepositories.findByProvinceCodeOrName(provinceCode, name);
    if (provinceCode == null && name == null) {
      logger.warn("request a parameter province code or province name");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    if (dataOfProvince == null) {
      logger.warn("parameter province code and province name did not matches");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(dataOfProvince);
  }

  /**
   * Multithreading: each threading get data one province
   */
  public ResponseEntity<List<Province>> findDataByStartDateAndEndDate(String startDate, String endDate) {
    List<Province> dataOfProvinceList = new ArrayList<>();
    try {

      if (startDate == null && endDate == null) {
        logger.warn("request parameter start date code and end date must not be null");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }

      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      AtomicInteger numberOfThread = new AtomicInteger();
      for (Integer provinceCode : provinceCodeList) {
        numberOfThread.incrementAndGet();
        CompletableFuture<Province> completableFuture =
          CompletableFuture.supplyAsync(() -> {
            Province province = provinceRepositories.findById(provinceCode).orElse(null);
            if (province != null) {
              province.getDataCovid()
                .getDataByDate()
                .removeIf(c -> !c.getDate().isBefore(LocalDate.parse(startDate)) && c.getDate().isAfter(LocalDate.parse(endDate)));
              logger.info("threading-" + numberOfThread + "select data covid by date of province " + province.getName() + "completed");
            }
            return province;
          });
        dataOfProvinceList.add(completableFuture.get());

      }
    }catch (IOException|InterruptedException|ExecutionException e){
      logger.error(e.getMessage());
    }
    return ResponseEntity.ok(dataOfProvinceList);
  }

}

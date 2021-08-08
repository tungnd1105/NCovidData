package com.ncovid.services;

import com.ncovid.data.multithreading.vietnam.DataCovidVietnam;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
  private ProvinceRepositories provinceRepositories;

  public ResponseEntity<Province> findOneByProvinceCodeOrName(Integer provinceCode, String name) {
    if (provinceCode == null && name == null) {
      logger.warn("requirement a parameter province code or name or short name");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    Province province = provinceRepositories.findByProvinceCodeOrName(provinceCode, name);
    if (province == null) {
      logger.warn("not found province ");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    province.getCovidData().getDataHistory().sort(Comparator.comparing(DataHistory::getDate));
    return ResponseEntity.ok(province);
  }

  public ResponseEntity<List<Province>> multithreading(String startDate, String endDate) {
    List<Province> provinceList = new ArrayList<>();
    try {
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      if (startDate == null && endDate == null) {
        logger.warn("requirement parameter start date and end date must not be null");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }
      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture<Province> completableFuture = CompletableFuture.supplyAsync(() -> {
          Province province = provinceRepositories.findById(provinceCode).orElse(null);
          if (province != null) {
            //filter and sort
            province.getCovidData()
              .getDataHistory().removeIf(c ->
              !c.getDate().isBefore(LocalDate.parse(startDate)) && c.getDate().isAfter(LocalDate.parse(endDate)));

            province.getCovidData().getDataHistory().sort(Comparator.comparing(DataHistory::getDate));
            logger.info("Completed select data of province " + province.getName());
          }
          return province;
        });
        provinceList.add(completableFuture.get());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok(provinceList);
  }

}

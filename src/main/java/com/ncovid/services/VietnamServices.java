package com.ncovid.services;

import com.ncovid.data.multithreading.vietnam.DataCovidVietnam;
import com.ncovid.dto.CovidDTO;
import com.ncovid.dto.VaccinationSiteDTO;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    CovidDTO covidDTO = new CovidDTO();
    if (name != null) {
      name = name.toUpperCase().substring(1);
    }
    if (provinceCode == null && name == null) {
      logger.warn("requirement a parameter province code or name or short name");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    Province province = provinceRepositories.findByProvinceCodeOrName(provinceCode, name);
    if (province == null) {
      logger.warn("not found ");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    province.getCovidData().getDataHistory().sort(Comparator.comparing(DataHistory::getDate));
    covidDTO.setProvinceCode(province.getProvinceCode());
    covidDTO.setName(province.getName());
    covidDTO.setCountry(province.getCountry());
    covidDTO.setShortName(province.getShortName());
    covidDTO.setTotalPopulation(province.getTotalPopulation());
    covidDTO.setPopOverEighteen(province.getPopOverEighteen());
    covidDTO.setCovidData(province.getCovidData());
    covidDTO.setVaccinationData(province.getVaccinationData());
    return ResponseEntity.ok(province);
  }

  public ResponseEntity<VaccinationSiteDTO> findVaccinationSite(Integer provinceCode, Integer districtsCode, Integer wardCode) {
    Province province = provinceRepositories.findProvince(provinceCode, districtsCode, wardCode);
    VaccinationSiteDTO vaccinationSiteDTO = new VaccinationSiteDTO();
    if (provinceCode == null && districtsCode == null && wardCode == null) {
      logger.warn("requirement a parameter province code or districts code or ward code");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    if (province == null) {
      logger.warn("not found ");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    vaccinationSiteDTO.setProvinceCode(province.getProvinceCode());
    vaccinationSiteDTO.setName(province.getName());
    vaccinationSiteDTO.setCountry(province.getCountry());
    vaccinationSiteDTO.setShortName(province.getShortName());
    vaccinationSiteDTO.setTotalPopulation(province.getTotalPopulation());
    vaccinationSiteDTO.setDistrictList(province.getDistrictList());
    vaccinationSiteDTO.setPopOverEighteen(province.getPopOverEighteen());
    return ResponseEntity.ok(vaccinationSiteDTO);
  }

  public ResponseEntity<List<CovidDTO>> multithreading(String startDate, String endDate) {
    List<CovidDTO> provinceList = new ArrayList<>();
    try {
      List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
      if (startDate == null && endDate == null) {
        logger.warn("requirement parameter start date and end date must not be null");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }

      for (Integer provinceCode : provinceCodeList) {
        CompletableFuture<CovidDTO> completableFuture = CompletableFuture.supplyAsync(() -> {
          Province province = provinceRepositories.findById(provinceCode).orElse(null);
          CovidDTO covidDTO = new CovidDTO();
          if (province != null) {
            //filter and sort
            province.getCovidData()
              .getDataHistory().removeIf(c ->
              !c.getDate().isBefore(LocalDate.parse(startDate)) && c.getDate().isAfter(LocalDate.parse(endDate)));

            covidDTO.setProvinceCode(province.getProvinceCode());
            covidDTO.setName(province.getName());
            covidDTO.setCountry(province.getCountry());
            covidDTO.setShortName(province.getShortName());
            covidDTO.setTotalPopulation(province.getTotalPopulation());
            covidDTO.setPopOverEighteen(province.getPopOverEighteen());
            covidDTO.setCovidData(province.getCovidData());
            covidDTO.setVaccinationData(province.getVaccinationData());
            covidDTO.getCovidData().getDataHistory().sort(Comparator.comparing(DataHistory::getDate));
            logger.info("Completed select data of province " + covidDTO.getName());
          }
          return covidDTO;
        });
        provinceList.add(completableFuture.get());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok(provinceList);
  }

}

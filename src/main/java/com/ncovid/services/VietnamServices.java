package com.ncovid.services;

import com.ncovid.data.multithreading.vietnam.DataCovid;
import com.ncovid.dto.ProvinceDTO;
import com.ncovid.dto.Tendency;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.vaccinationSite.Site;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.vaccinationSite.SiteRepositories;
import com.ncovid.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.services.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: receive and process requests get data covid  from the controller
 */

@Service
public class VietnamServices {

  public static Logger logger = LoggerFactory.getLogger(DataCovid.class);

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;

  @Autowired
  private CovidStatisticsRepositories covidStatisticsRepositories;

  @Autowired
  private SiteRepositories siteRepositories;


  public ResponseEntity<ProvinceDTO> findOneBy(Integer provinceCode, String name, String shortName) {
    ProvinceDTO provinceDTO = provinceRepositories.findByProvinceCodeOrNameOrShortName(provinceCode, name, shortName);
    return ResponseEntity.ok(provinceDTO);
  }

  public ResponseEntity<List<ProvinceDTO>> findAllProvince(){
    List<ProvinceDTO> provinceDTOList = provinceRepositories.findAllProvince();
    return ResponseEntity.ok(provinceDTOList);
  }

  public  ResponseEntity<Page<ProvinceDTO>> findAllProvince(Integer pageNumber, Integer pageSize){
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<ProvinceDTO> page = provinceRepositories.findAllProvince(pageable);
    return ResponseEntity.ok(page);
  }

  public ResponseEntity<List<DataHistory>> findByDate(Integer provinceCode, String provinceName, Integer numberDay) {
    List<DataHistory> dataHistory = dataHistoryRepositories.findByDate(provinceCode, provinceName, UtilDate.today.minusDays(numberDay), UtilDate.today);
    if (dataHistory == null) {
      logger.warn("parameter did not matches province");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    if (provinceCode == null && provinceName == null) {
      logger.warn("requirement a parameter province code or province name or shortname");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(dataHistory);
  }

  public ResponseEntity<Page<Site>> searchVaccinationSite(Integer pageNumber, Integer pageSize,
                                                          Integer provinceCode, Integer districtCode, Integer wardCode) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<Site> page = siteRepositories.findAllSite(pageable, provinceCode, districtCode, wardCode);
    return ResponseEntity.ok(page);
  }

  public ResponseEntity<Page<Site>> findAllVaccinationSite(Integer pageNumber, Integer pageSize) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<Site> page = siteRepositories.findAll(pageable);
    return ResponseEntity.ok(page);
  }

  public ResponseEntity<List<String>> findAllName() {
    List<String> listName = provinceRepositories.findName();
    return ResponseEntity.ok(listName);
  }


}

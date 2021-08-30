package com.ncovid.services;

import com.ncovid.data.multithreading.vietnam.DataCovidVietnam;
import com.ncovid.dto.DataTableCovidDTO;
import com.ncovid.dto.ProvinceDTO;
import com.ncovid.entity.vietnam.CovidStatistics;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.vaccinationSite.Site;
import com.ncovid.repositories.vietnam.CovidStatisticsRepositories;
import com.ncovid.repositories.vietnam.DataHistoryRepositories;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.vaccinationSite.SiteRepositories;
import com.ncovid.util.ProvinceOfVietnam;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

  @Autowired
  private DataHistoryRepositories dataHistoryRepositories;

  @Autowired
  private CovidStatisticsRepositories covidStatisticsRepositories;

  @Autowired
  private SiteRepositories siteRepositories;

  public ResponseEntity<ProvinceDTO> findOneByProvinceCodeOrProvinceName(Integer provinceCode, String name, String shortname) {
    Province province = provinceRepositories.findByProvinceCodeOrName(provinceCode, name, shortname);
    ProvinceDTO provinceDTO = ProvinceDTO.TransferProvinceDTO(province);
    if (provinceCode == null && name == null && shortname == null) {
      logger.warn("requirement a parameter province code or province name or shortname");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    if (province == null) {
      logger.warn("parameter did not matches province");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(provinceDTO);
  }

  public ResponseEntity<List<DataHistory>> findByDate(Integer provinceCode, String provinceName, Integer numberDay) {
    List<DataHistory> dataHistory = dataHistoryRepositories.findByDate(provinceCode, provinceName,UtilDate.today.minusDays(numberDay), UtilDate.today);
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

  public ResponseEntity<List<DataTableCovidDTO>> findAlal() throws IOException, InterruptedException, ExecutionException {
    Long a = System.currentTimeMillis();
    List<DataTableCovidDTO> dtoList = new ArrayList<>();
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    for (Integer provinceCode : provinceCodeList) {
      CompletableFuture<DataTableCovidDTO> data = CompletableFuture.supplyAsync(() -> {
        CovidStatistics covidStatistics = covidStatisticsRepositories.findCovidStatistics(provinceCode);
        return DataTableCovidDTO.TransferDataTableCovidDTO(covidStatistics);
      });
      CompletableFuture.allOf(data).join();
      dtoList.add(data.get());
    }
    return ResponseEntity.ok(dtoList);
  }

  public ResponseEntity<Page<Site>> searchVaccinationSite(
    Integer pageNumber, Integer pageSize,
    Integer provinceCode, Integer districtCode, Integer wardCode
  ) {
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

package com.ncovid.controller.api;

import com.ncovid.dto.ProvinceDTO;
import com.ncovid.entity.vietnam.DataHistory;
import com.ncovid.entity.vietnam.vaccinationSite.Site;
import com.ncovid.services.VietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.controller
 * @project NCovidData
 * @Date 26/07/2021
 * description class: handle request get data covid just in Vietnam from client
 */

@RestController
@RequestMapping("api/v1/covid-data/Vietnam")
public class VietnamAPI {

  @Autowired
  private VietnamServices vietnamServices;


  @GetMapping("province/all-name")
  private ResponseEntity<List<String>> findAllName() {
    return vietnamServices.findAllName();
  }

  @GetMapping
  private ResponseEntity<List<ProvinceDTO>> findAllProvince() {
    return vietnamServices.findAllProvince();
  }

  @GetMapping("page")
  private ResponseEntity<Page<ProvinceDTO>> findAllProvince(
    @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize
  ) {
    return vietnamServices.findAllProvince(pageNumber, pageSize);
  }

  @GetMapping("province")
  private ResponseEntity<ProvinceDTO> findOneByProvinceCodeOrProvinceName(
    @RequestParam(required = false) Integer provinceCode,
    @RequestParam(required = false) String provinceName,
    @RequestParam(required = false) String shortName
  ) {
    return vietnamServices.findOneBy(provinceCode, provinceName, shortName);
  }

  @GetMapping("province/find-by-date")
  private ResponseEntity<List<DataHistory>> findByDate(
    @RequestParam(required = false) Integer provinceCode,
    @RequestParam(required = false) String provinceName,
    @RequestParam(required = false) Integer numberDays
  ) {
    return vietnamServices.findByDate(provinceCode, provinceName, numberDays);
  }

  @GetMapping("search-vaccination-site")
  private ResponseEntity<Page<Site>> findAllSite(
    @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize,
    @RequestParam(defaultValue = "1") Integer provinceCode,
    @RequestParam(defaultValue = "1") Integer districtCode,
    @RequestParam(defaultValue = "1") Integer wardCode
  ) {
    return vietnamServices.searchVaccinationSite(pageNumber, pageSize, provinceCode, districtCode, wardCode);
  }

  @GetMapping("find-all-vaccination-site")
  private ResponseEntity<Page<Site>> findAllSite(
    @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    return vietnamServices.findAllVaccinationSite(pageNumber, pageSize);
  }
}

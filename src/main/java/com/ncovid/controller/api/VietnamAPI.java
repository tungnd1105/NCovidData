package com.ncovid.controller.api;

import com.ncovid.dto.CovidDTO;
import com.ncovid.dto.VaccinationSiteDTO;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.services.VietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping("find-one")
  private ResponseEntity<Province> findOneByProvinceCodeOrName(
    @RequestParam(required = false) Integer provinceCode,
    @RequestParam(required = false) String name) {
    return vietnamServices.findOneByProvinceCodeOrName(provinceCode, name);
  }

  @GetMapping("find-all-filter-by-date")
  private ResponseEntity<List<CovidDTO>> findAll(
    @RequestParam String startDate, @RequestParam String endDate
  ) {
    return vietnamServices.multithreading(startDate, endDate);
  }

  @GetMapping("search-vaccination-site")
  private ResponseEntity<VaccinationSiteDTO> findVaccinationSite(
    @RequestParam(required = false) Integer provinceCode,
    @RequestParam(required = false) Integer districtsCode,
    @RequestParam(required = false) Integer wardCode
  ) {
    return vietnamServices.findVaccinationSite(provinceCode, districtsCode, wardCode);
  }
}

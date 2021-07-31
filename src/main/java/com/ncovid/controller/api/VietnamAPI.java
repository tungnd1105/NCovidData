package com.ncovid.controller.api;

import com.ncovid.entity.vietnam.Province;
import com.ncovid.services.multithreading.vietnam.VietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

  @GetMapping("data-by-date")
  private ResponseEntity<List<Province>> findAllDataVietnam(
    @RequestParam(defaultValue = "2021-04-26", required = false) String start_date,
    @RequestParam(defaultValue = "2021-07-28", required = false) String end_date
  ) {
    return vietnamServices.findDataByStartDateAndEndDate(start_date, end_date);
  }

  @GetMapping("data-of-province")
  private ResponseEntity<Province> findOneByProvince(
    @RequestParam(required = false) Integer provinceCode,
    @RequestParam(required = false) String name
  ) throws IOException {
    return vietnamServices.findDataByOneProvince(provinceCode, name);
  }

}

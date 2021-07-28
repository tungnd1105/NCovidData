package com.ncovid.controller.api;

import com.ncovid.entity.StatisticalDataVietnam;
import com.ncovid.services.multithreading.DataVietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author ndtun
 * @package com.ncovid.controller
 * @project NCovidData
 * @Date 26/07/2021
 * description class: handle request from client
 */

@RestController
@RequestMapping("api/v1/covid-data/Vietnam")
public class DataVietnamAPI {

  @Autowired
  private DataVietnamServices dataVietnamServices;

  @GetMapping("data-by-date")
  private ResponseEntity<List<StatisticalDataVietnam>> findAllDataVietnam(
    @RequestParam(defaultValue = "2021-04-27", required = false) String start_date,
    @RequestParam(defaultValue = "2021-07-27", required = false) String end_date
  ) throws IOException, ExecutionException, InterruptedException {
    return dataVietnamServices.runMultithreadingFindAllData(start_date, end_date);
  }
//
//  @GetMapping("{provinceCode}")
//  private ResponseEntity<StatisticalDataVietnam> findOneByProvince(@PathVariable Integer provinceCode) {
//    return dataVietnamServices.findOneByProvince(provinceCode);
//  }

}

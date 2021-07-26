package com.ncovid.controller;

import com.ncovid.entity.StatisticalDataVietnam;
import com.ncovid.services.multithreading.DataVietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping
  private ResponseEntity<List<StatisticalDataVietnam>> findAllDataVietnam() throws IOException, ExecutionException, InterruptedException {
    return dataVietnamServices.runMultithreadingFindAllData();
  }

  @GetMapping("{province}")
  private ResponseEntity<StatisticalDataVietnam> findOneByProvince(@PathVariable String province){
    return dataVietnamServices.findOneByProvince(province);
  }

}

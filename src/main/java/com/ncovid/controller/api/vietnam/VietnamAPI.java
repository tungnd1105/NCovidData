package com.ncovid.controller.api.vietnam;

import com.ncovid.entity.vietnam.Province;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.services.multithreading.vietnam.VietnamServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  private ProvinceRepositories vietnamServices;


  @GetMapping("{province}")
  public ResponseEntity<Province> test(@PathVariable Integer province){
    Province province1 = vietnamServices.findById(province).orElse(null);
    return ResponseEntity.ok(province1);
  }

}

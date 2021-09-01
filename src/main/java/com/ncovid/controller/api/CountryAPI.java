package com.ncovid.controller.api;

import com.ncovid.entity.countries.Country;
import com.ncovid.services.CountryServices;
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
 * @package com.ncovid.controller.api.country
 * @project NCovidData
 * @Date 07/08/2021
 */

@RestController
@RequestMapping("api/v1/covid-data/Country")
public class CountryAPI {

  @Autowired
  private CountryServices countryServices;

  @GetMapping("all-name")
  private ResponseEntity<List<String>> findAllName(){
    return countryServices.findAllName();
  }

  @GetMapping
  private ResponseEntity<Page<Country>> findAll(
    @RequestParam(defaultValue = "0") Integer pageNumber,
    @RequestParam(defaultValue = "10") Integer pageSize
  ) {
    return countryServices.findAll(pageNumber, pageSize);
  }

  @GetMapping("find-one")
  private ResponseEntity<Country> findOneByIdOrAlphaCode(
    @RequestParam(required = false) String id,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String alphaCode
  ) {
    return countryServices.findOneByNameOrAlphaCode(id, name, alphaCode);
  }
}

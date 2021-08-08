package com.ncovid.controller.api;

import com.ncovid.entity.countries.Country;
import com.ncovid.services.CountryServices;
import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping
  private ResponseEntity<List<Country>> findAll() {
    return countryServices.findAll();
  }

  @GetMapping("get-data-country")
  private ResponseEntity<Country> findOneByIdOrAlphaCode(
    @RequestParam(required = false) String id,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String alphaCode
  ) {
    return countryServices.findOneByNameOrAlphaCode(id, name, alphaCode);
  }
}

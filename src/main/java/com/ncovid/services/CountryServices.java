package com.ncovid.services;

import com.ncovid.entity.countries.Country;
import com.ncovid.repositories.countries.CountryRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.services.multithreading.vietnam
 * @project NCovidData
 * @Date 07/08/2021
 */
@Service
public class CountryServices {

  public static Logger logger = LoggerFactory.getLogger(CountryServices.class);

  @Autowired
  private CountryRepositories countryRepositories;

  public ResponseEntity<List<String>> findAllName() {
    List<String> allName = countryRepositories.findName();
    return ResponseEntity.ok(allName);
  }

  public ResponseEntity<Country> findOneByNameOrAlphaCode(String id, String name, String alpha2Code) {
    if(id != null){
      id = id.toUpperCase();
    }
    Country country = countryRepositories.findByIdOrAlpha2CodeOrName(id, name, alpha2Code);
    if (id == null & name == null & alpha2Code == null) {
      logger.warn("requirement a parameter id or name or short alpha2Code");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    if (country == null) {
      logger.warn("not found province ");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(country);
  }

  public ResponseEntity<Page<Country>> findAll(Integer pageNumber, Integer pageSize) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<Country> countryList = countryRepositories.findAll(pageable);
    return ResponseEntity.ok(countryList);
  }

}

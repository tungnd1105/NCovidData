package com.ncovid.services;

import com.ncovid.entity.countries.Country;
import com.ncovid.repositories.countries.CountryRepositories;
import com.ncovid.util.AlphaCodeCountry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

  public ResponseEntity<Country> findOneByNameOrAlphaCode(String id, String name, String alpha2Code) {
    Country country = countryRepositories.findByIdOrAlpha2CodeOrName(id, name, alpha2Code);
    if (id == null & name == null & alpha2Code == null) {
      logger.warn("requirement a parameter id or name or short alpha2Code");
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    if (country == null) {
      logger.warn("not found province ");
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    logger.info("completed select data of " + country.getName());
    return ResponseEntity.ok(country);
  }

  public ResponseEntity<List<Country>> findAll() {
    List<Country> countryList = new ArrayList<>();
    try {
      List<String> alphaCodeList = AlphaCodeCountry.getAllAlphaCode();
      for (String alphaCode : alphaCodeList) {
        CompletableFuture<Country> completableFuture = CompletableFuture.supplyAsync(() -> countryRepositories.findById(alphaCode).orElse(null));
        countryList.add(completableFuture.get());
        logger.info("completed select data of" + completableFuture.get().getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok(countryList);
  }

}
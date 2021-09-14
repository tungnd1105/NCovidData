package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
@package com.ncovid.repositories.countries
@project NCovidData
@author ndtun
@Date 06/08/2021
*/public interface CountryRepositories extends JpaRepository<Country, String> {

  @Query("SELECT a FROM Country a WHERE a.id = ?1 or a.name = ?2 or a.alpha2Code = ?3 ")
  Country findByIdOrAlpha2CodeOrName(String id, String name, String alpha2Code);

  Page<Country> findAll(Pageable pageable);

  @Query("SELECT a.name FROM Country a")
  List<String> findName();

  @Query("SELECT DISTINCT TRIM(a.subregion) FROM Country a ")
  List<String> findRegion();

  @Query("SELECT a FROM Country a " +
    " INNER JOIN Covid_Statistics_Country c ON c.country.id = a.id" +
    " WHERE a.subregion = ?1")
  List<Country> findCountryByRegion(String region);
}

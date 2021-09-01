package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
@package com.ncovid.repositories.countries
@project NCovidData
@author ndtun
@Date 06/08/2021
*/public interface CountryRepositories extends JpaRepository<Country, String> {

  @Query("SELECT a FROM Country a WHERE a.id = ?1 or a.name = ?2 or a.alpha2Code = ?3 ")
  Country findByIdOrAlpha2CodeOrName(String id, String name, String alpha2Code);

  Page<Country> findAll(Pageable pageable);

}

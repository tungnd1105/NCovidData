package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.Country;
import org.springframework.data.jpa.repository.JpaRepository;

/**
@package com.ncovid.repositories.countries
@project NCovidData
@author ndtun
@Date 06/08/2021
*/public interface CountryRepositories extends JpaRepository<Country, String> {
}

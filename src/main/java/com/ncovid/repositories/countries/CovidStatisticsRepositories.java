package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.CovidStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.repositories.countries
 * @project NCovidData
 * @Date 04/08/2021
 */
@Repository(value = "Covid_Statistical_Repositories_Country")
public interface CovidStatisticsRepositories extends JpaRepository<CovidStatistics, Integer> {

  @Query("SELECT a.totalCase FROM Covid_Statistics_Country a" )
  List<String> findByTotalCase();

  @Query("SELECT a.totalDeaths FROM Covid_Statistics_Country a" )
  List<String> findByTotalDeaths();

  @Query("SELECT a.totalRecovered FROM Covid_Statistics_Country a" )
  List<String> findByTotalRecovered();

}

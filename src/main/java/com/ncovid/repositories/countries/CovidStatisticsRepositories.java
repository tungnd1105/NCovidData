package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.CovidStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.countries
 * @project NCovidData
 * @Date 04/08/2021
 */
@Repository(value = "Covid_Statistical_Repositories_Country")
public interface CovidStatisticsRepositories extends JpaRepository<CovidStatistics, Integer> {
}

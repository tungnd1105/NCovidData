package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.CovidStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
@Repository(value = "Covid_Statistics_Repositories_Vietnam")
public interface CovidStatisticsRepositories extends JpaRepository<CovidStatistics, Integer> {

}

package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.StatisticalCovid;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
public interface StatisticalCovidRepositories extends JpaRepository<StatisticalCovid, Integer> {

}

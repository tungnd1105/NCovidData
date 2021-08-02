package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.StatisticalCovid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
@Repository
public interface StatisticalCovidRepositories extends JpaRepository<StatisticalCovid, Integer> {

}

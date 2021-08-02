package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.StatisticalVaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 */
@Repository
public interface StatisticalVaccineRepositories extends JpaRepository<StatisticalVaccine, Integer> {
}

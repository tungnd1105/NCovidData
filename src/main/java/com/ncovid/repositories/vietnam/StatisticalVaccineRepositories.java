package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.StatisticalVaccine;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 */
public interface StatisticalVaccineRepositories extends JpaRepository<StatisticalVaccine, Integer> {
}

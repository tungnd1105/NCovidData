package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.VaccinationStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 */
@Repository(value = "Vaccination_Statistics_Repositories_Vietnam" )
public interface VaccinationStatisticsRepositories extends JpaRepository<VaccinationStatistics, Integer> {
}

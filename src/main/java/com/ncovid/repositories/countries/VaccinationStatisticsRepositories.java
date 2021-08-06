package com.ncovid.repositories.countries;

import com.ncovid.entity.countries.VaccinationStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.countries
 * @project NCovidData
 * @Date 05/08/2021
 */
@Repository(value = "Vaccination_Statistics_Repositories_Country")
public interface VaccinationStatisticsRepositories extends JpaRepository<VaccinationStatistics, Integer> {
}

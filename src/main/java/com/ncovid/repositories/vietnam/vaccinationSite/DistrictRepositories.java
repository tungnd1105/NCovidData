package com.ncovid.repositories.vietnam.vaccinationSite;

import com.ncovid.entity.vietnam.vaccinationSite.District;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam.vaccinationSite
 * @project NCovidData
 * @Date 22/08/2021
 */
public interface DistrictRepositories extends JpaRepository<District, Integer> {
}

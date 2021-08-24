package com.ncovid.repositories.vietnam.vaccinationSite;

import com.ncovid.entity.vietnam.vaccinationSite.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam.vaccinationSite
 * @project NCovidData
 * @Date 22/08/2021
 */
public interface WardRepositories extends JpaRepository<Ward, Integer> {
}

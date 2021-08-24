package com.ncovid.repositories.vietnam.vaccinationSite;

import com.ncovid.entity.vietnam.vaccinationSite.Site;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam.vaccinationSite
 * @project NCovidData
 * @Date 24/08/2021
 */
public interface SiteRepositories extends JpaRepository<Site, Integer> {
}

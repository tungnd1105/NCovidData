package com.ncovid.repositories;

import com.ncovid.entity.StatisticalDataVietnam;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
public interface SDVietnamRepositories extends JpaRepository<StatisticalDataVietnam, Integer> {
}

package com.ncovid.repositories;

import com.ncovid.entity.HistoryDataCovidVN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
@Repository
public interface HistoryDataCovidVNRepositories extends JpaRepository<HistoryDataCovidVN, Integer> {
}

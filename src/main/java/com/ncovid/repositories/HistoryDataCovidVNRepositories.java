package com.ncovid.repositories;

import com.ncovid.entity.HistoryDataCovidVN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
@Repository
public interface HistoryDataCovidVNRepositories extends JpaRepository<HistoryDataCovidVN, Integer> {


  @Query("SELECT a FROM HistoryDataCovidVN a WHERE a.province = ?1")
  List<HistoryDataCovidVN> findByProvince(String province);
}

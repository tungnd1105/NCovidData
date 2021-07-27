package com.ncovid.repositories;

import com.ncovid.entity.StatisticalDataVietnam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
public interface SDVietnamRepositories extends JpaRepository<StatisticalDataVietnam, Integer> {

  @Query("SELECT a FROM StatisticalDataVietnam a WHERE a.provinceCode = ?1")
  StatisticalDataVietnam findByProvinceCode(Integer provinceCode);

}

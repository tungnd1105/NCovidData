package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 27/07/2021
 */
@Repository
public interface ProvinceRepositories extends JpaRepository<Province, Integer> {

  @Query("SELECT a FROM Province a WHERE a.provinceCode = ?1 OR a.name = ?2 ")
  Province findByProvinceCodeOrName(Integer provinceCode, String name);

  @Query("SELECT a FROM Province a WHERE a.provinceCode = ?1")
  Province findByProvinceCode(Integer provinceCode);



}

package com.ncovid.repositories.vietnam;

import com.ncovid.dto.ProvinceDTO;
import com.ncovid.entity.vietnam.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 27/07/2021
 */
@Repository
public interface ProvinceRepositories extends JpaRepository<Province, Integer> {


  @Query("SELECT new com.ncovid.dto.ProvinceDTO(p.name,c.updateTime," +
    " c.cases, c.newCases, c.casesPercent, c.yesterdayCases, c.deaths, c.newDeaths, c.deathsPercent, c.yesterdayDeaths," +
    " v.totalFullyInjected, v.totalInjectedOneDose, v.totalInjected, v.totalVaccineReality, " +
    " v.fullyInjectedPercent, v.injectedOneDosePercent, v.totalInjectedPercent, v.totalVaccinePercent ) " +
    " FROM Province p " +
    " INNER JOIN Covid_Statistics_Vietnam c ON c.province.provinceCode = p.provinceCode " +
    " INNER JOIN Vaccination_Statistics_Vietnam v ON v.province.provinceCode = p.provinceCode " +
    " WHERE p.provinceCode = ?1 OR p.name = ?2 or p.shortName = ?3 ")
  ProvinceDTO findByProvinceCodeOrNameOrShortName(Integer provinceCode, String name, String shortName);

  @Query("SELECT new com.ncovid.dto.ProvinceDTO(p.name,c.updateTime," +
    " c.cases, c.newCases, c.casesPercent, c.yesterdayCases, c.deaths, c.newDeaths, c.deathsPercent, c.yesterdayDeaths," +
    " v.totalFullyInjected, v.totalInjectedOneDose, v.totalInjected, v.totalVaccineReality, " +
    " v.fullyInjectedPercent, v.injectedOneDosePercent, v.totalInjectedPercent, v.totalVaccinePercent ) " +
    " FROM Province p " +
    " INNER JOIN Covid_Statistics_Vietnam c ON c.province.provinceCode = p.provinceCode " +
    " INNER JOIN Vaccination_Statistics_Vietnam v ON v.province.provinceCode = p.provinceCode " +
    " ORDER BY c.newCases   DESC  ")
  List<ProvinceDTO> findAllProvince();

  @Query("SELECT new com.ncovid.dto.ProvinceDTO(p.name,c.updateTime," +
    " c.cases, c.newCases, c.casesPercent, c.yesterdayCases, c.deaths, c.newDeaths, c.deathsPercent, c.yesterdayDeaths," +
    " v.totalFullyInjected, v.totalInjectedOneDose, v.totalInjected, v.totalVaccineReality, " +
    " v.fullyInjectedPercent, v.injectedOneDosePercent, v.totalInjectedPercent, v.totalVaccinePercent ) " +
    " FROM Province p " +
    " INNER JOIN Covid_Statistics_Vietnam c ON c.province.provinceCode = p.provinceCode " +
    " INNER JOIN Vaccination_Statistics_Vietnam v ON v.province.provinceCode = p.provinceCode ")
  Page<ProvinceDTO> findAllProvince(Pageable pageable);

  @Query("SELECT a.name FROM Province a ")
  List<String> findName();

}

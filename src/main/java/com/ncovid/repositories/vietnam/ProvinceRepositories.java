package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 27/07/2021
 */
@Repository
public interface ProvinceRepositories extends JpaRepository<Province, Integer>, PagingAndSortingRepository<Province, Integer> {

  @Query("SELECT a from Province a WHERE a.provinceCode = :provinceCode or a.name = :name or a.shortName = :shortName")
  Province findByProvinceCodeOrName(
    @Param("provinceCode") Integer provinceCode,
    @Param("name") String name,
    @Param("shortName") String shortName);

}

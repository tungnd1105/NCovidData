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
public interface ProvinceRepositories extends JpaRepository<Province, Integer>, PagingAndSortingRepository<Province,Integer> {

  @Query("SELECT a from Province a WHERE a.provinceCode = :provinceCode or a.name Like %:name")
  Province findByProvinceCodeOrName(@Param("provinceCode") Integer provinceCode, @Param("name") String name);

  @Query("select a from Province a " +
    " inner join Covid_Statistics_Vietnam c on a.provinceCode = c.province.provinceCode" +
    " inner join DataHistory t on c.id = t.covidData.id" +
    " inner join District d on d.province.provinceCode = a.provinceCode " +
    " inner join Ward w on w.district.districtCode = d.districtCode " +
    " inner join Site s on s.ward.wardCode = w.wardCode " +
    " where a.provinceCode = ?1 or d.districtCode = ?2 or w.wardCode = ?3 ")
  Province findProvince(Integer provinceCode,Integer districtsCode, Integer wardCode );
}

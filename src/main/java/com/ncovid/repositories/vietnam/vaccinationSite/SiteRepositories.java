package com.ncovid.repositories.vietnam.vaccinationSite;

import com.ncovid.entity.vietnam.vaccinationSite.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ndtun
 * @package com.ncovid.repositories.vietnam.vaccinationSite
 * @project NCovidData
 * @Date 24/08/2021
 */
public interface SiteRepositories extends JpaRepository<Site, Integer> {

  @Query("SELECT s FROM Site s " +
    "inner join Ward w on w.wardCode = s.ward.wardCode " +
    " inner join District d on d.districtCode = w.district.districtCode " +
    " inner join Province p on p.provinceCode = d.province.provinceCode" +
    " where p.provinceCode = :provinceCode and d.districtCode = :districtCode and w.wardCode = :wardCode")
  Page<Site> findAllSite(
    Pageable pageable, @RequestParam("provinceCode") Integer provinceCode,
    @RequestParam("districtCode") Integer districtCode, @RequestParam("wardCode") Integer wardCode);

  Page<Site> findAll(Pageable pageable);

}

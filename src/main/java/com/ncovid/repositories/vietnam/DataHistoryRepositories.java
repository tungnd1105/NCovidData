package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.DataHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.repositories
 * @project NCovidData
 * @Date 25/07/2021
 */
@Repository
public interface DataHistoryRepositories extends JpaRepository<DataHistory, Integer> {

  @Query( "SELECT a from DataHistory a " +
    " inner join Covid_Statistics_Vietnam c on a.covidData.id = c.id " +
    " inner join Province p on p.provinceCode = c.province.provinceCode" +
    " where c.province.provinceCode = ?1 or p.name = ?2 and a.date >= ?3  and a.date <= ?4 order by a.date ASC ")
  List<DataHistory> findByDate(Integer provinceCode, String provinceName, LocalDate startDate, LocalDate endDate );

  @Query("SELECT a FROM DataHistory a inner join Covid_Statistics_Vietnam  c on c.id = a.covidData.id" +
    " where c.province.provinceCode = ?1 and a.date = ?2")
  DataHistory findByDate( Integer provinceCode ,LocalDate date);

}

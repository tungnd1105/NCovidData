package com.ncovid.repositories.vietnam;

import com.ncovid.entity.vietnam.DataHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

  @Query("SELECT a FROM DataHistory a WHERE a.provinceCode = ?1")
  List<DataHistory> findByProvinceCode(int province);

  @Query("SELECT a FROM DataHistory a WHERE a.date = ?1")
  DataHistory findByDate(LocalDate date);

}

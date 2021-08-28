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

  @Query( "SELECT a from DataHistory a where a.date = :date")
  DataHistory findByDate(@Param("date")LocalDate date);

}

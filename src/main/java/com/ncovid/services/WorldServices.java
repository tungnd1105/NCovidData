package com.ncovid.services;

import com.ncovid.dto.WorldDTO;
import com.ncovid.repositories.countries.CovidStatisticsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ndtun
 * @package com.ncovid.services
 * @project NCovidData
 * @Date 08/08/2021
 */
@Service
public class WorldServices {

  @Autowired
  private CovidStatisticsRepositories dataCovidRepositories;

  public ResponseEntity<WorldDTO> statisticsWorld() {
    WorldDTO worldDTO = new WorldDTO();
    // get list and convert string to Integer
    List<Integer> totalCases = dataCovidRepositories.findByTotalCase().stream().map(Integer::valueOf).collect(Collectors.toList());
    List<Integer> totalDeaths = dataCovidRepositories.findByTotalDeaths().stream().map(Integer::valueOf).collect(Collectors.toList());
    List<Integer> totalRecovered = dataCovidRepositories.findByTotalRecovered().stream().map(Integer::valueOf).collect(Collectors.toList());
    worldDTO.setTotalCases(totalCases.stream().mapToInt(Integer::intValue).sum());
    worldDTO.setTotalDeaths(totalDeaths.stream().mapToInt(Integer::intValue).sum());
    worldDTO.setTotalRecovered(totalRecovered.stream().mapToInt(Integer::intValue).sum());
    return ResponseEntity.ok(worldDTO);
  }

}

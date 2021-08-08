package com.ncovid.services;

import com.ncovid.repositories.countries.CovidStatisticsRepositories;
import com.ncovid.response.World;
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

  public ResponseEntity<World> statisticsWorld() {
    World world = new World();
    // get list and convert string to Integer
    List<Integer> totalCases = dataCovidRepositories.findByTotalCase().stream().map(Integer::valueOf).collect(Collectors.toList());
    List<Integer> totalDeaths = dataCovidRepositories.findByTotalDeaths().stream().map(Integer::valueOf).collect(Collectors.toList());
    List<Integer> totalRecovered = dataCovidRepositories.findByTotalRecovered().stream().map(Integer::valueOf).collect(Collectors.toList());
    world.setTotalCases(totalCases.stream().mapToInt(Integer::intValue).sum());
    world.setTotalDeaths(totalDeaths.stream().mapToInt(Integer::intValue).sum());
    world.setTotalRecovered(totalRecovered.stream().mapToInt(Integer::intValue).sum());
    return ResponseEntity.ok(world);
  }

}

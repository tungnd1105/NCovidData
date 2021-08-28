package com.ncovid.controller.api;


import com.ncovid.dto.WorldDTO;
import com.ncovid.services.WorldServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ndtun
 * @package com.ncovid.controller.api
 * @project NCovidData
 * @Date 08/08/2021
 */

@RestController
@RequestMapping("api/v1/covid-data/World")
public class WorldAPI {

  @Autowired
  private WorldServices worldServices;

  @GetMapping
  private ResponseEntity<WorldDTO> statisticsWorld() {
    return worldServices.statisticsWorld();
  }
}

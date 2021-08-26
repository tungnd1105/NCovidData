package com.ncovid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ndtun
 * @package com.ncovid.response
 * @project NCovidData
 * @Date 08/08/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorldDTO {

  private Integer totalCases;
  private Integer totalDeaths;
  private Integer totalRecovered;
}

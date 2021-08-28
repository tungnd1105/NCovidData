package com.ncovid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ndtun
 * @package com.ncovid.dto
 * @project NCovidData
 * @Date 25/08/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CovidDTO {
  private String  updateTime;
  private Integer cases;
  private Integer deaths;
  private Integer recovered;
  private Integer today;
  private Integer yesterday;
  private Double  casesPercent;
  private Double  deathsPercent;
  private Double  recoveredPercent;

}

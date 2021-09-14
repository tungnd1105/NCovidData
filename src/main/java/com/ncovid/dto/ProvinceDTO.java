package com.ncovid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ndtun
 * @package com.ncovid.dto
 * @project VaccinationStatistics.java
 * @Date 12/09/2021
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDTO {

  private String name;
  private String updateTime;
  private Integer cases;
  private Integer newCases;
  private Double casesPercent;
  private Integer yesterdayCases;
  private Integer deaths;
  private Integer newDeaths;
  private Double deathsPercent;
  private Integer yesterdayDeaths;
  private Integer fullyInjected;
  private Integer injectedOneDose;
  private Integer totalInjected;
  private Integer totalVaccine;
  private Double fullyInjectedPercent;
  private Double injectedOneDoesPercent;
  private Double injectedPercent;
  private Double totalVaccinePercent;


}

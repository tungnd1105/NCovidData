package com.ncovid.dto;

import com.ncovid.entity.vietnam.CovidStatistics;
import com.ncovid.entity.vietnam.VaccinationStatistics;
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


  private Integer provinceCode;
  private String  name;
  private String  type;
  private String  shortName;
  private Integer totalPopulation;
  private Integer popOverEighteen;
  private String  country;
  private CovidStatistics covidData;
  private VaccinationStatistics vaccinationData;
}

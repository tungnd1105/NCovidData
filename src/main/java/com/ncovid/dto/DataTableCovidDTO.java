package com.ncovid.dto;

import com.ncovid.entity.vietnam.CovidStatistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ndtun
 * @package com.ncovid.dto
 * @project CovidStatistics.java
 * @Date 28/08/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTableCovidDTO {
  private Integer provinceCode;
  private String  name;
  private Integer cases;
  private Integer deaths;
  private Integer recovered;
  private Integer today;
  private Integer yesterday;

  public static DataTableCovidDTO TransferDataTableCovidDTO(CovidStatistics covidStatistics){
    DataTableCovidDTO dataTableCovidDTO = new DataTableCovidDTO();
    dataTableCovidDTO.setProvinceCode(covidStatistics.getProvince().getProvinceCode());
    dataTableCovidDTO.setName(covidStatistics.getProvince().getName());
    dataTableCovidDTO.setCases(covidStatistics.getCases());
    dataTableCovidDTO.setDeaths(covidStatistics.getDeaths());
    dataTableCovidDTO.setRecovered(covidStatistics.getRecovered());
    dataTableCovidDTO.setToday(covidStatistics.getToday());
    dataTableCovidDTO.setYesterday(covidStatistics.getYesterday());
    return dataTableCovidDTO;
  }
}

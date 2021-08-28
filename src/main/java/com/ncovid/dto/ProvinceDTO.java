package com.ncovid.dto;

import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.VaccinationStatistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ndtun
 * @package com.ncovid.dto
 * @project CovidStatistics.java
 * @Date 28/08/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDTO {

  public static Logger logger = LoggerFactory.getLogger(ProvinceDTO.class);

  private Integer provinceCode;
  private String name;
  private String type;
  private String shortName;
  private Integer totalPopulation;
  private Integer popOverEighteen;
  private String country;
  private CovidDTO covidData;
  private VaccinationStatistics vaccinationData;


  public static ProvinceDTO TransferProvinceDTO(Province province) {
    ProvinceDTO provinceDTO = new ProvinceDTO();
    if(province == null){
      return null;
    }
    CovidDTO covidDTO = new CovidDTO();
    covidDTO.setUpdateTime(province.getCovidData().getUpdateTime());
    covidDTO.setCases(province.getCovidData().getCases());
    covidDTO.setDeaths(province.getCovidData().getDeaths());
    covidDTO.setRecovered(province.getCovidData().getRecovered());
    covidDTO.setCasesPercent(province.getCovidData().getCasesPercent());
    covidDTO.setDeathsPercent(province.getCovidData().getDeathsPercent());
    covidDTO.setRecoveredPercent(province.getCovidData().getRecoveredPercent());
    covidDTO.setToday(province.getCovidData().getToday());
    covidDTO.setYesterday(province.getCovidData().getYesterday());
    provinceDTO.setProvinceCode(province.getProvinceCode());
    provinceDTO.setName(province.getName());
    provinceDTO.setShortName(province.getShortName());
    provinceDTO.setCountry(province.getCountry());
    provinceDTO.setType(province.getType());
    provinceDTO.setTotalPopulation(province.getTotalPopulation());
    provinceDTO.setPopOverEighteen(province.getPopOverEighteen());
    provinceDTO.setVaccinationData(province.getVaccinationData());
    provinceDTO.setCovidData(covidDTO);

    return provinceDTO;
  }
}

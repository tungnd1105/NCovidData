package com.ncovid.dto;

import com.ncovid.entity.vietnam.vaccinationSite.District;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;
import java.util.Set;

/**
 * @author ndtun
 * @package com.ncovid.dto
 * @project NCovidData
 * @Date 25/08/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationSiteDTO {

  private Integer provinceCode;
  private String  name;
  private String  type;
  private String  shortName;
  private Integer totalPopulation;
  private Integer popOverEighteen;
  private String  country;
  private List<District> districtList;
}

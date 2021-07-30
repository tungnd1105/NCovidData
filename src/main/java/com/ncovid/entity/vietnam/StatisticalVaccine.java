package com.ncovid.entity.vietnam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 * description class: reporter data vaccine by province or city in Vietnam
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticalVaccine implements Serializable {

  private static final long serialVersionUID = 5986887121427178891L;

  @Id
  private Integer provinceCode;
  private String  updateTime;
  private Integer totalVaccinated;
  private Integer totalTwiceInjected;
  private Integer totalOnceInjected;
  private Integer totalVaccinationLocation;
  private Integer totalVaccineAllocated;
  private Integer totalVaccineReality;

}

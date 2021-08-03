package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 27/07/2021
 * description class: information of all province in Vietnam
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province implements Serializable {

  private static final long serialVersionUID = -7233922340182006222L;

  @Id
  private Integer provinceCode;
  private String  name;
  private String  type;
  private String  shortName;
  private Integer totalPopulation;
  private Integer popOverEighteen;
  private String  country;

  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private StatisticalCovid covidData;

  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private StatisticalVaccine vaccineData;

}

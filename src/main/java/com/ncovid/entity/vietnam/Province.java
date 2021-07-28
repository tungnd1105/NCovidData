package com.ncovid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
public class ProvinceVietnam implements Serializable {

  private static final long serialVersionUID = -7233922340182006222L;

  @Id
  private Integer provinceCode;
  private String  name;
  private String  type;
  private Integer totalPopulation;

  @OneToOne
  @JoinColumn(name = "id_data_covid", referencedColumnName = "provinceCode")
  private StatisticalDataVietnam DataCovid;
}

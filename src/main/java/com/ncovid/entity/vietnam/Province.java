package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ncovid.entity.vietnam.vaccinationSite.District;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 27/07/2021
 * description class: information of all province in Vietnam
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Province implements Serializable {

  private static final long serialVersionUID = -7233922340182006222L;

  @Id
  private Integer provinceCode;
  @Column(length = 100)
  private String  name;

  @Column(length = 100)
  private String  type;

  @Column(length = 100)
  private String  shortName;

  private Integer totalPopulation;
  private Integer popOverEighteen;

  @Column(length = 100)
  private String  country;

  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private CovidStatistics covidData;

  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private VaccinationStatistics vaccinationData;

  @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<District> districtList;

}

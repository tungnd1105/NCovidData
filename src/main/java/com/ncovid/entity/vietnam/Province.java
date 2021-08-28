package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ncovid.entity.vietnam.vaccinationSite.District;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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


  @JsonManagedReference
  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private CovidStatistics covidData;

  @JsonManagedReference
  @OneToOne(mappedBy = "province", cascade = CascadeType.ALL )
  private VaccinationStatistics vaccinationData;

  @JsonManagedReference
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<District> districtList;

}

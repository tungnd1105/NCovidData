package com.ncovid.entity.countries;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity.countries
 * @project NCovidData
 * @Date 04/08/2021
 * description class: detail data about of country
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country implements Serializable {

  private static final long serialVersionUID = 525749752702185255L;

  @Id
  private String id;

  @Column(length = 100)
  private String name;

  private Integer population;

  @Column(length = 100)
  private String capital;

  @Column(length = 100)
  private String region;

  @Column(length = 100)
  private String subregion;

  @Column(length = 10)
  private String alpha2Code;

  @Column(length = 10)
  private String numericCode;

  @OneToOne(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private CovidStatistics covidData;

  @OneToOne(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private VaccinationStatistics vaccinationData;

}

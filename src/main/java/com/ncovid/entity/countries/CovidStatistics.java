package com.ncovid.entity.countries;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
 * description class: reporter data covid 19 of all Country
 */
@Entity(name = "covid_Statistics_country")
@Table(name = "covid_statistics_country")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CovidStatistics implements Serializable {

  private static final long serialVersionUID = -9184910512145544847L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer totalCase;
  private Integer newCases;
  private Integer totalDeaths;
  private Integer newDeaths;
  private Integer totalRecovered;
  private Integer newRecovered;
  private Integer activeCases;
  private Integer seriousCritical;
  private Integer totalTest;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_country")
  private Country country;

}

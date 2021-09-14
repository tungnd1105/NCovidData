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
@Entity(name = "Covid_Statistics_Country")
@Table(name = "covid_statistics_country")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CovidStatistics implements Serializable {

  private static final long serialVersionUID = -9184910512145544847L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String updateTime;
  private String totalCase = "0";
  private String newCases  ="0";
  private String totalDeaths = "0";
  private String newDeaths ="0" ;
  private String totalRecovered =  "0";
  private String newRecovered ="0";
  private String activeCases ="0";
  private String seriousCritical ="0";
  private String totalTest ="0";

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_country")
  private Country country;

}

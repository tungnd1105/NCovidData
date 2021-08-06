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
 * description class: reporter data vaccination all country
 */
@Entity(name = "vaccination_statistics_country")
@Table(name = "vaccination_statistics_country")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationStatistics implements Serializable {

  private static final long serialVersionUID = 1470514817043492968L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer totalVaccine;
  private Integer newVaccine;
  private Integer totalInjectedOneDose;
  private Integer totalFullyInjected;
  private Double totalVaccinePercent;
  private Double fullyInjectedPercent;
  private Double injectedOneDosePercent;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_country")
  private Country country;

}

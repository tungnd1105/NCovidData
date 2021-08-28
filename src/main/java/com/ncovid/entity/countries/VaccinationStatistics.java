package com.ncovid.entity.countries;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ndtun
 * @package com.ncovid.entity.countries
 * @project NCovidData
 * @Date 04/08/2021
 * description class: reporter data vaccination all country
 */
@Entity(name = "Vaccination_Statistics_Country")
@Table(name = "vaccination_statistics_country")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationStatistics implements Serializable {

  private static final long serialVersionUID = 1470514817043492968L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String updateTime;
  private String totalVaccine;
  private String newVaccine;
  private String totalInjectedOneDose;
  private String totalFullyInjected;
  private Double totalVaccinePercent;
  private Double fullyInjectedPercent;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double injectedOneDosePercent;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_country")
  private Country country;

}

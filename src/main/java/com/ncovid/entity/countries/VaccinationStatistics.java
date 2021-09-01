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
  private String totalVaccine = "0";
  private String newVaccine ="0";
  private String totalInjectedOneDose ="0";
  private String totalFullyInjected ="0";
  private Double totalVaccinePercent = 0.0;
  private Double fullyInjectedPercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double injectedOneDosePercent;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_country")
  private Country country;

}

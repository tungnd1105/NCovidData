package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 * description class: reporter data vaccine by province or city in Vietnam
 */

@Entity(name = "Vaccination_Statistics_Vietnam")
@Table(name = "vaccination_statistics_vietnam")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccinationStatistics implements Serializable {

  private static final long serialVersionUID = 5986887121427178891L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String updateTime;
  private Integer totalInjected = 0;
  private Integer totalFullyInjected = 0;
  private Integer totalInjectedOneDose = 0;
  private Integer totalVaccinationLocation = 0;
  private Integer totalVaccineAllocated = 0;
  private Integer totalVaccineReality = 0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double fullyInjectedPercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double injectedOneDosePercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double totalVaccinePercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double totalInjectedPercent = 0.0;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "province_Code")
  private Province province;

}

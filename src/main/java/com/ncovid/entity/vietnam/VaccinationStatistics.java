package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity.vietnam
 * @project NCovidData
 * @Date 29/07/2021
 * description class: reporter data vaccine by province or city in Vietnam
 */

@Entity(name ="Vaccination_Statistics_Vietnam" )
@Table(name ="vaccination_statistics_vietnam" )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationStatistics implements Serializable {

  private static final long serialVersionUID = 5986887121427178891L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String  updateTime;
  private Integer totalInjected;
  private Integer totalFullyInjected;
  private Integer totalInjectedOneDose;
  private Integer totalVaccinationLocation;
  private Integer totalVaccineAllocated;
  private Integer totalVaccineReality;
  private float   fullyInjectedPercent;
  private float   injectedOneDosePercent;
  private float   totalVaccinePercent;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "province_Code" )
  private Province province;

}

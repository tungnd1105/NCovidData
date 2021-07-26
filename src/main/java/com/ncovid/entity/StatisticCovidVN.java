package com.ncovid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 25/07/2021
 * description class: reporter data covid 19 of all province/city in Vietnam
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticCovidVN {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String province;
  private Integer cases;
  private Integer deaths;
  private Integer domesticCases;
  private Integer entryCases;

  @JoinColumn(name = "id_data_by_date", referencedColumnName = "id")
  @ManyToOne
  private HistoryDataCovidVN dataByDate;
}

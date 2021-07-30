package com.ncovid.entity.vietnam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class StatisticalCovid {

  @Id
  private Integer provinceCode;
  private String  updateTime;
  private Integer cases;
  private Integer deaths;
  private Integer domesticCases;
  private Integer entryCases;
  private Integer today;
  private Integer yesterday;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "data_by_date",//
    joinColumns = @JoinColumn(name= "id"),
    inverseJoinColumns = @JoinColumn(name="id_data"))
  private List<DataHistory> dataByDate = new ArrayList<>();

}

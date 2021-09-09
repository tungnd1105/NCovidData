package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 25/07/2021
 * description class: reporter data covid 19 of all province/city in Vietnam
 */
@Entity(name = "Covid_Statistics_Vietnam")
@Table(name = "covid_statistics_vietnam")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CovidStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String updateTime;
  private Integer cases = 0;
  private Integer deaths = 0;
  private Integer recovered = 0;
  private Integer today = 0;
  private Integer treating=0;
  private Integer yesterday = 0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double casesPercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double deathsPercent = 0.0;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double recoveredPercent = 0.0;


  @JsonManagedReference
  @OneToOne
  @JoinColumn(name = "province_Code")
  private Province province;

  @JsonBackReference
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "covidData", cascade = CascadeType.ALL)
  private List<DataHistory> dataHistory;


}

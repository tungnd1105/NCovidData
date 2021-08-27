package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
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
  private String  updateTime;
  private Integer cases;
  private Integer deaths;
  private Integer recovered;
  private Integer today;
  private Integer yesterday;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double  casesPercent;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double  deathsPercent;

  @NumberFormat(style = NumberFormat.Style.PERCENT)
  private Double  recoveredPercent;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "province_Code")
  private Province province;

  @OneToMany(mappedBy = "covidData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  @Fetch(value = FetchMode.SELECT)
  private List<DataHistory> dataHistory;

}

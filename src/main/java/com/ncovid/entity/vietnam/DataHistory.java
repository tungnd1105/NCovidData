package com.ncovid.entity.vietnam;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 25/07/2021
 * description class: reporter data NCovid by date of all province in Vietnam
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataHistory implements Serializable {

  private static final long serialVersionUID = 6371070049025062271L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private LocalDate date;
  private Integer value;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_data_covid")
  @JsonBackReference
  private StatisticalCovid covidData;

}

package com.ncovid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class DataHistoryVietnam implements Serializable {

  private static final long serialVersionUID = 6371070049025062271L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String province;
  private LocalDate date;
  private Integer value;

}

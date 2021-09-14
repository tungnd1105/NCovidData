package com.ncovid.entity.vietnam.vaccinationSite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ndtun
 * @package com.ncovid.entity.vietnam.VaccinationSite
 * @project NCovidData
 * @Date 22/08/2021
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Site implements Serializable {

  private static final long serialVersionUID = 1061918077859524101L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 200)
  private String name;

  @Column(length = 200)
  private String address;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "ward_Code")
  private Ward ward;
}

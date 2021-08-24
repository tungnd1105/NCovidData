package com.ncovid.entity.vietnam.vaccinationSite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
public class Ward implements Serializable {


  private static final long serialVersionUID = 1588988617837646074L;
  @Id
  private Integer wardCode;

  @Column(length = 100)
  private String warName;

  @Column(length = 50)
  private String divisionType;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "district_Code")
  private District district;

  @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<Site> siteList;
}

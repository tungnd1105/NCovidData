package com.ncovid.entity.vietnam.vaccinationSite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ncovid.entity.vietnam.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
public class District implements Serializable {


  private static final long serialVersionUID = -3804135201980648185L;

  @Id
  private Integer districtCode;

  @Column(length = 100)
  private String districtName;

  @Column(length = 50)
  private String divisionType;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "province_Code")
  private Province province;

  @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  @Fetch(value=FetchMode.SELECT)
  private List<Ward> wardList;

}

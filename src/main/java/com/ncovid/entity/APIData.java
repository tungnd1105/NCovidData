package com.ncovid.entity;

import com.ncovid.util.Util;
import lombok.ToString;

/**
 * @author ndtun
 * @package com.ncovid.entity
 * @project NCovidData
 * @Date 24/08/2021
 */

@ToString
public enum APIData {
  covidByProvince("https://static.pipezero.com/covid/data.json"),
  vaccinationsByProvince("https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-statistics/all"),
  detailDistricts("https://provinces.open-api.vn/api/?depth=3"),
  detailCountry("https://restcountries.eu/rest/v2/all"),
  covidByCountry("https://www.worldometers.info/coronavirus/"),
  vaccinationsByCountry("https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/latest/owid-covid-latest.csv");

  private final String api;

  APIData(String api) {
    this.api = api;
  }

  public String getApi(){
    return this.api;
  }
}

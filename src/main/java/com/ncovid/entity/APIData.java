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
  detailCountry("https://restcountries.eu/rest/v2/all"),
  covidByCountry("https://www.worldometers.info/coronavirus/"),
  vaccinationsByCountry("https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/latest/owid-covid-latest.csv"),
  detailProvince("https://tiemchungcovid19.gov.vn/api/province/public/all"),
  population("https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccine-allocate/province-detail"),
  covidByCurrent("https://ncov.vncdc.gov.vn/v2/vietnam/by-current?start_time=2021-04-27" + "&end_time=" + Util.today),
  covidByProvince("https://ncov.vncdc.gov.vn/v2/vietnam/province-type?start_time=2021-04-27" + "&end_time=" + Util.today),
  vaccinationsByProvince("https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-statistics/all"),
  detailDistricts("https://provinces.open-api.vn/api/?depth=3"),
  vaccinationSite("https://tiemchungcovid19.gov.vn/api/public/dashboard/vaccination-location/search?page=0&size=10&");

  private final String api;

  APIData(String api) {
    this.api = api;
  }

  public String getApi(){
    return this.api;
  }
}

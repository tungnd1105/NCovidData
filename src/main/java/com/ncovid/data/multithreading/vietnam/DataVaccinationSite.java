package com.ncovid.data.multithreading.vietnam;


import com.ncovid.entity.APIData;
import com.ncovid.entity.vietnam.Province;
import com.ncovid.entity.vietnam.vaccinationSite.District;
import com.ncovid.entity.vietnam.vaccinationSite.Site;
import com.ncovid.entity.vietnam.vaccinationSite.Ward;
import com.ncovid.repositories.vietnam.ProvinceRepositories;
import com.ncovid.repositories.vietnam.vaccinationSite.DistrictRepositories;
import com.ncovid.repositories.vietnam.vaccinationSite.SiteRepositories;
import com.ncovid.repositories.vietnam.vaccinationSite.WardRepositories;
import com.ncovid.util.Message;
import com.ncovid.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ndtun
 * @package com.ncovid.data.multithreading.vietnam
 * @project NCovidData
 * @Date 22/08/2021
 * description class: insert data all vaccination site in Vietnam
 */
@Service
public class DataVaccinationSite {

  public static Logger logger = LoggerFactory.getLogger(DataVaccinationSite.class);

  @Autowired
  private ProvinceRepositories provinceRepositories;

  @Autowired
  private SiteRepositories siteRepositories;
  @Autowired
  private DistrictRepositories districtRepositories;

  @Autowired
  private WardRepositories wardRepositories;

  private JSONArray insertDataInfoOfDistricts(JSONObject jsonObject) {
    Province provinceDB = provinceRepositories.findById(Integer.parseInt(jsonObject.get("code").toString())).orElse(null);
    JSONArray jsonArray = jsonObject.getJSONArray("districts");
    if (provinceDB != null) {
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject object = jsonArray.getJSONObject(k);
        District district = new District();
        district.setDistrictCode(object.getInt("code"));
        district.setDistrictName(object.getString("name"));
        district.setDivisionType(object.getString("division_type"));
        district.setProvince(provinceDB);
        districtRepositories.save(district);
      }
    }
    return jsonArray;
  }

  private List<Ward> insertDataInfoOfWard(JSONArray jsonArray) {
    List<Ward> wardList = new ArrayList<>();
    for (int k = 0; k < jsonArray.length(); k++) {
      JSONObject object = jsonArray.getJSONObject(k);
      District districtDB = districtRepositories.findById(object.getInt("code")).orElse(null);
      if (districtDB != null) {
        JSONArray jsonArray2 = object.getJSONArray("wards");
        for (int a = 0; a < jsonArray2.length(); a++) {
          JSONObject jsonObject = jsonArray2.getJSONObject(a);
          Ward ward = new Ward();
          ward.setWardCode(jsonObject.getInt("code"));
          ward.setWarName(jsonObject.getString("name"));
          ward.setDivisionType(jsonObject.getString("division_type"));
          ward.setDistrict(districtDB);
          wardList.add(ward);
        }
      }
    }
    wardRepositories.saveAll(wardList);
    return wardList;
  }

  private void insertDataVaccinationSite(List<Ward> wardList) throws IOException, InterruptedException {
    for (Ward ward : wardList) {
      String api = Util.urlDataVaccinationSite +
        "provinceCode=" + String.format("%02d", ward.getDistrict().getProvince().getProvinceCode()) +
        "&districtCode=" + String.format("%03d", ward.getDistrict().getDistrictCode()) +
        "&wardCode=" + String.format("%05d", ward.getWardCode());
      List<Site> siteList = new ArrayList<>();
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(api));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject object = jsonArray.getJSONObject(k);
        Site site = new Site();
        site.setName(object.getString("name"));
        site.setAddress(object.getString("address"));
        site.setWard(ward);
        siteList.add(site);
      }
      siteRepositories.saveAll(siteList);
      logger.info("Threading-" + Thread.currentThread().getId() + Message.insertDataVaccinationSite + ward.getWarName());
    }
  }


  /**
   * if data not yet in databases run insert new
   * use multithreading to performance optimization
   * each threading will be insert data vaccination site of province
   * each threading flow task
   * insertDataInfoOfDistricts -> insertDataInfoOfWard ->  insertDataVaccinationSite
   */
//  @EventListener(ApplicationReadyEvent.class)
//  @Async("taskExecutor")
  public void runMultithreading() throws IOException{
    List<District> checkData = districtRepositories.findAll();
    if (checkData.size() == 0) {
      JSONArray jsonArray = new JSONArray((new String(Files.readAllBytes(Paths.get(Util.dataProvinceVN.getAbsolutePath())))));
      for (int k = 0; k < jsonArray.length(); k++) {
        JSONObject jsonObject = jsonArray.getJSONObject(k);
        CompletableFuture<List<Ward>> completableFuture =
          CompletableFuture.supplyAsync(() -> insertDataInfoOfDistricts(jsonObject)).thenApply(this::insertDataInfoOfWard);

        completableFuture.thenRun(() -> {
          try {
            insertDataVaccinationSite(completableFuture.get());
          } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
          }
        });
      }
    }
  }
}

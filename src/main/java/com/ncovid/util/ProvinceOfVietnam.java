package com.ncovid.util;

import com.ncovid.entity.APIData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.util
 * @project NCovidData
 * @Date 25/07/2021
 * description class: all province of Vietnam
 */

@Service
public class ProvinceOfVietnam {

  public static Logger logger = LoggerFactory.getLogger(ProvinceOfVietnam.class);

  public static List<Integer> getAllProvince() throws IOException{
    logger.info("Stating get all province code of Vietnam");
    JSONArray dataJsonProvince = new JSONArray(new String(Files.readAllBytes(Paths.get(Util.dataProvinceVN.getAbsolutePath()))));
    List<Integer> listProvince = new ArrayList<>();
    for (int i = 0; i < dataJsonProvince.length(); i++) {
      JSONObject province = (JSONObject) dataJsonProvince.get(i);
      listProvince.add(Integer.parseInt(province.get("code").toString()));
    }
    return listProvince;
  }

}

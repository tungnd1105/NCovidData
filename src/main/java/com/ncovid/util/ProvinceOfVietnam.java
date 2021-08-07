package com.ncovid.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

  public static List<Integer> getAllProvince() throws IOException, InterruptedException {
    logger.info("Stating get all province code of Vietnam");
    logger.info("staring assign task for threading by province code ");
    JSONArray dataJsonProvince = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));
    List<Integer> listProvince = new ArrayList<>();
    for (int i = 0; i < dataJsonProvince.length(); i++ ){
      JSONObject province = (JSONObject) dataJsonProvince.get(i);
      listProvince.add(Integer.parseInt(province.get("ma").toString()));
    }

    return listProvince;
  }

}

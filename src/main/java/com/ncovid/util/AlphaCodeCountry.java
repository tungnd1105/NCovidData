package com.ncovid.util;

import com.ncovid.entity.APIData;
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
 * @Date 04/08/2021
 */
@Service
public class AlphaCodeCountry {

  public static Logger logger = LoggerFactory.getLogger(AlphaCodeCountry.class);

  public static List<String> getAllAlphaCode() throws IOException, InterruptedException {
    logger.info("starting get all alpha code of country");
    logger.info("staring assign task for threading by alpha code of country");
    List<String> alphaCodeList = new ArrayList<>();
    JSONArray dataJson = new JSONArray(Util.fetchDataJson(APIData.detailCountry));
    for (int i = 0; i < dataJson.length(); i++) {
      JSONObject alphaCode = (JSONObject) dataJson.get(i);
      alphaCodeList.add(alphaCode.getString("alpha3Code"));
    }
    return alphaCodeList;
  }
}

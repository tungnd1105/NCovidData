package com.ncovid.util;

import org.json.JSONArray;
import org.json.JSONObject;
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


  public static List<String> getAllAlphaCode() throws IOException, InterruptedException {
    List<String> alphaCodeList = new ArrayList<>();
    JSONArray dataJson = new JSONArray(Util.fetchDataJson(Util.urlDetailCountry));
    for (int i = 0; i < dataJson.length(); i++) {
      JSONObject alphaCode = (JSONObject) dataJson.get(i);
      alphaCodeList.add(alphaCode.getString("alpha3Code"));
    }
    return alphaCodeList;
  }
}

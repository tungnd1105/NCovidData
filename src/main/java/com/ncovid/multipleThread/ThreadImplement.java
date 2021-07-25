package com.ncovid.multipleThread;

import com.ncovid.entity.HistoryDataCovidVN;
import com.ncovid.repositories.HistoryDataCovidVNRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import com.ncovid.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author ndtun
 * @package com.ncovid.request
 * @project NCovidData
 * @Date 25/07/2021
 * description: use multithreading insert data Covid of province in Vietnam by date
 */

public class ThreadImplement implements Runnable {

  public static Logger logger = LoggerFactory.getLogger(ProvinceOfVietnam.class);

  private final String threadName;
  private final String province;
  private Thread thread;
  private final HistoryDataCovidVNRepositories dataCovidVNRepositories;

  public ThreadImplement(String name, String provinceName, HistoryDataCovidVNRepositories repositories) {
    threadName = name;
    province = provinceName;
    dataCovidVNRepositories = repositories;
  }


  /**
   * use multithreading to insert data over 1000 record
   * each thread insert data covid by date  of province in Vietnam
   * */
  @Override
  public void run() {
    logger.info(threadName + " " + "is running");
    try {
      JSONArray jsonArray = new JSONArray(Util.fetchDataJson(Util.urlDataByCurrent));

      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
        // check the name of the province to assign tasks for thread  and insert data
        if (jsonObject.get("tinh").toString().matches(province)) {
          for (LocalDate date = Util.startDate; date.isBefore(Util.today.plusDays(1)); date = date.plusDays(1)) {
            JSONObject dataBydate = (JSONObject) jsonObject.get("data");
            HistoryDataCovidVN historyData = new HistoryDataCovidVN();
            historyData.setProvince(jsonObject.get("tinh").toString());
            historyData.setDate(date);
            historyData.setValue(Integer.parseInt(dataBydate.get(date.toString()).toString()));
            dataCovidVNRepositories.save(historyData);
          }
        }

      }
      Thread.sleep(50);
    } catch (InterruptedException | IOException ex) {
      logger.error(threadName + " " + "interrupted");
    }
    logger.info(threadName + " " + "completed");
  }

  /** method for start thread when create new thread */
  public void start() {
    if (thread == null) {
      thread = new Thread(this, threadName);
      thread.start();
    }
  }
}

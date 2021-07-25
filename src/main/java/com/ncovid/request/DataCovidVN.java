package com.ncovid.request;

import com.ncovid.multipleThread.ThreadImplement;
import com.ncovid.repositories.HistoryDataCovidVNRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author ndtun
 * @package com.ncovid.request
 * @project NCovidData
 * @Date 25/07/2021
 */

@Service
public class DataCovidVN {

  @Autowired
  HistoryDataCovidVNRepositories repositories;


  @EventListener(ApplicationReadyEvent.class)
  public void insertDataByDate() throws IOException, InterruptedException {
    // get all province of Vietnam
    List<String> allProvince = ProvinceOfVietnam.getAllProvince();
    int a = 0;
    // loop all province in list
    for(String province : allProvince){
      //create new thread and assign tasks for thread by province
      ThreadImplement thread = new ThreadImplement("thread-" + a++, province, repositories);
      //start thread
      thread.start();
    }
  }

}

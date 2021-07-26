package com.ncovid.services.multithreading;

import com.ncovid.data.multithreading.DataCovidVietnam;
import com.ncovid.entity.StatisticalDataVietnam;
import com.ncovid.repositories.SDVietnamRepositories;
import com.ncovid.util.ProvinceOfVietnam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ndtun
 * @package com.ncovid.services.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: receive and process requests from the controller
 */

@Service
public class DataVietnamServices {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

  @Autowired
  private SDVietnamRepositories sdVietnamRepositories;

  public ResponseEntity<StatisticalDataVietnam> findOneByProvince(String province) {
    StatisticalDataVietnam SDVietnam = sdVietnamRepositories.findByProvince(province);
    try {
      if (province.trim().isEmpty()) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }

      if (SDVietnam == null) {
        logger.error("not found province");
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      logger.error("handle exception" + e);
    }
    logger.info("find data covid of" + " " + province + " " + "completed");
    return ResponseEntity.ok(SDVietnam);
  }

  private StatisticalDataVietnam findDataByProvince(int threadingNumber, String province) {
    logger.info("threading-" + threadingNumber + " " + "is running select data covid of" + " " + province);
    StatisticalDataVietnam SDVietnam = new StatisticalDataVietnam();
    if (!province.trim().isEmpty()) {
      SDVietnam = sdVietnamRepositories.findByProvince(province);
    }
    logger.info("threading-" + threadingNumber + " " + "is running select data covid of" + " " + province + " " + "completed");
    return SDVietnam;
  }

  public ResponseEntity<List<StatisticalDataVietnam>> runMultithreadingFindAllData() throws IOException,
    InterruptedException,
    ExecutionException {
    List<String> allProvince = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();
    List<StatisticalDataVietnam> SDVietnamList = new ArrayList<>();
    for (String province : allProvince) {
      numberOfThread.incrementAndGet();
      CompletableFuture<StatisticalDataVietnam> completableFuture =
        CompletableFuture.supplyAsync(() -> findDataByProvince(numberOfThread.get(), province));
      SDVietnamList.add(completableFuture.get());
    }
    return ResponseEntity.ok(SDVietnamList);
  }

}

package com.ncovid.services.multithreading;

import com.ncovid.data.multithreading.DataCovidVietnam;
import com.ncovid.entity.DataHistoryVietnam;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

  public ResponseEntity<StatisticalDataVietnam> findOneByProvince(Integer provinceCode) {
    StatisticalDataVietnam SDVietnam = sdVietnamRepositories.findByProvinceCode(provinceCode);
    try {
      if (provinceCode == null) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }

      if (SDVietnam == null) {
        logger.error("not found province");
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      logger.error("handle exception" + e);
    }
    logger.info("find data covid of" + " " + provinceCode + " " + "completed");
    return ResponseEntity.ok(SDVietnam);
  }

  private StatisticalDataVietnam findDataByProvince(int threadingNumber, Integer provinceCode) {
    logger.info("threading-" + threadingNumber + " " + "is running select data covid of" + " " + provinceCode);
    StatisticalDataVietnam SDVietnam = new StatisticalDataVietnam();
    if (provinceCode != null) {
      SDVietnam = sdVietnamRepositories.findByProvinceCode(provinceCode);
    }
    logger.info("threading-" + threadingNumber + " " + "is running select data covid of" + " " + provinceCode + " " + "completed");
    return SDVietnam;
  }

  public ResponseEntity<List<StatisticalDataVietnam>> runMultithreadingFindAllData(String startDate, String endDate)
    throws IOException, InterruptedException, ExecutionException {
    List<Integer> provinceCodeList = ProvinceOfVietnam.getAllProvince();
    AtomicInteger numberOfThread = new AtomicInteger();
    List<StatisticalDataVietnam> SDVietnamList = new ArrayList<>();
    for (Integer provinceCode : provinceCodeList) {
      numberOfThread.incrementAndGet();
      CompletableFuture<StatisticalDataVietnam> completableFuture =
        CompletableFuture.supplyAsync(() -> findDataByProvince(numberOfThread.get(), provinceCode));
      SDVietnamList.add(completableFuture.get());

      // sort by date
      SDVietnamList.forEach(a -> a.getDataByDate().sort(Comparator.comparing(DataHistoryVietnam::getDate)));

      if (startDate != null && endDate != null) {
        /* filter data by start date, end date */
        SDVietnamList
          .forEach(e -> e.getDataByDate()
            .removeIf(b ->
              !b.getDate().isBefore(LocalDate.parse(startDate)) && b.getDate().isAfter(LocalDate.parse(endDate))));
      }
    }

    return ResponseEntity.ok(SDVietnamList);
  }
}

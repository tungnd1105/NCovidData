package com.ncovid.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ndtun
 * @package com.ncovid.util
 * @project CovidStatistics.java
 * @Date 27/08/2021
 */
public class UtilDate {

  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
  public static String timeUpdate = formatterDateTime.format(LocalDateTime.now());

  public static String date = "2021-04-27";
  public static LocalDate startDate = LocalDate.parse(date, formatter);
  public static LocalDate today = LocalDate.now();

}

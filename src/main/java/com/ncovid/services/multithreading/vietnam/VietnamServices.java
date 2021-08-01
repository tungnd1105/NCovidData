package com.ncovid.services.multithreading.vietnam;

import com.ncovid.data.multithreading.vietnam.DataCovidVietnam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ndtun
 * @package com.ncovid.services.multithreading
 * @project NCovidData
 * @Date 26/07/2021
 * description class: receive and process requests get data covid  from the controller
 */

@Service
public class VietnamServices {

  public static Logger logger = LoggerFactory.getLogger(DataCovidVietnam.class);

}

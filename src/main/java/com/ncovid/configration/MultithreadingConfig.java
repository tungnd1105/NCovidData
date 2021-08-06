package com.ncovid.configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author ndtun
 * @package com.ncovid.configration
 * @project NCovidData
 * @Date 25/07/2021
 */
@Configuration
@EnableAsync
public class MultithreadingConfig {

  @Bean(name ="taskExecutor")
  public Executor taskExecutor(){
    ThreadPoolTaskExecutor executor= new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Thread-");
    executor.initialize();
    executor.shutdown();
    executor.getActiveCount();
    return executor;
  }

}

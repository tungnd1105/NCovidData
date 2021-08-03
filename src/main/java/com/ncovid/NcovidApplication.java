package com.ncovid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NcovidApplication {
  public static void main(String[] args) {
    SpringApplication.run(NcovidApplication.class, args);
  }

}
